package model;

import model.entity.Location;
import model.entity.biology.Biology;
import model.entity.biology.animal.Animal;
import model.entity.biology.animal.Human;
import model.entity.biology.animal.HumanPlayer;
import model.entity.biology.animal.Wolf;
import model.entity.biology.plant.Plant;
import model.interfaces.Cell;
import view.View;

import java.applet.Applet;
import java.applet.AudioClip;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 每一回合的处理
 */
public class Round {
    private Field field;
    private View view;
    //惨叫声
    private static AudioClip screech;
    //肚子好饿周星驰
    private static AudioClip hungry;
    //婴儿开心笑声
    private static AudioClip laughter;
    private static AudioClip music;
    private AudioClip wolfAudio;
    private AudioClip sheepAudio;

    private ThreadPoolExecutor threadPool;

    public Round(Field field, View view, ThreadPoolExecutor threadPoolExecutor) {
        this.field = field;
        this.view = view;
        this.threadPool = threadPoolExecutor;
        try {
            music = Applet.newAudioClip(this.getClass().getResource("/resource/背景音乐.wav"));
            music.loop();
            screech = Applet.newAudioClip(this.getClass().getResource("/resource/惨叫声.wav"));
            hungry = Applet.newAudioClip(this.getClass().getResource("/resource/肚子好饿周星驰.wav"));
            laughter = Applet.newAudioClip(this.getClass().getResource("/resource/婴儿开心笑声.wav"));
            wolfAudio = Applet.newAudioClip(this.getClass().getResource("/resource/狼叫声.wav"));
            sheepAudio = Applet.newAudioClip(this.getClass().getResource("/resource/羊叫声.wav"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 每一回合的处理
     *
     * @param version 最新版本号
     */
    public void oneRound(int version) {
//        int threadNum = threadPool.getMaximumPoolSize();
//        CountDownLatch countDownLatch = new CountDownLatch(threadNum);
//        // 将Field按行分割为threadNum块, 每一块分配一个线程来执行
//        for (int thread = 0; thread < threadNum; thread++) {
//            int i = thread;
//            threadPool.execute(() -> {
//            int part = field.getHeight() / threadNum;
//            int iPlusOne = i + 1;
//            for (int row = i * part; row < (threadNum == iPlusOne ? field.getHeight() : part * iPlusOne); row++) {
//                for (int col = 0; col < field.getWidth(); col++) {
//                    cellActionStrategy(row, col, version);
//                    countDownLatch.countDown();
//                }
//            }
//            });
//        }
//        try {
//            //保证线程都执行完之后才结束回合
//            countDownLatch.await();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
        for (int row = 0; row < field.getHeight(); row++) {
            for (int col = 0; col < field.getWidth(); col++) {
                cellActionStrategy(row, col, version);
            }
        }
    }

    /**
     * 每一个Cell的行为策略
     *
     * @param row     所在行
     * @param col     所在列
     * @param version 最新版本号
     */
    private void cellActionStrategy(int row, int col, int version) {
        Biology biology = (Biology) field.getCell(row, col);
        if (biology != null && biology.compareVersion(version) && biology.grow()) {
            if (biology instanceof Animal) {
                //如果是动物
                Animal animal = (Animal) biology;
                if (!(animal instanceof HumanPlayer)) {
                    /*
                    move
                    注意: 移动之后位置改变了,不能用row和col来表示动物位置
                     */
                    Location loc = animal.move(animal.lookAround(field));
                    if (loc != null) {
                        field.move(row, col, loc);
                    }
                    /*
                    eat
                     */
                    if (animal instanceof Wolf) {
                        //狼的捕食范围为周围两圈
                        Wolf wolf = (Wolf) animal;
                        List<Biology> listBiology = field.getNeighbour(animal.getRow(), animal.getColumn(), 2);
//                            if (an instanceof Biology) {
//                                listBiology.add((Biology) an);
//                            }

                        if (!listBiology.isEmpty()) {
                            Biology prey = wolf.eat(listBiology);
                            if (prey != null) {
                                if (prey instanceof Human) {
//                                    screech.play();
                                }
                                field.replace(wolf, prey);
                            }
                        }
                    } else {
                        //其他动物捕食范围为周围一圈
                        ArrayList<Biology> listBiology = new ArrayList<>();
                        for (Cell an : field.getNeighbour(animal.getRow(), animal.getColumn(), 1)) {
                            if (an instanceof Biology) {
                                listBiology.add((Biology) an);
                            }
                        }
                        if (!listBiology.isEmpty()) {
                            Biology prey = animal.eat(listBiology);
                            if (prey != null) {
                                field.replace(animal, prey);
                                if (prey instanceof Wolf) {
                                    wolfAudio.play();
                                }
                            }
                        }
                    }
                    /*
                    breed
                     */
                    Cell baby = animal.breed();
                    if (baby != null) {
                        field.placeNewBiology(animal.getRow(), animal.getColumn(), baby);
                    }
                }
//                else {//actor
//                    // breed
//                    Cell baby = animal.breed();
//                    if (baby != null) {
//                        field.placeRandomAdj(row, col, baby);
//                        if (isContains.add(baby)) {
//                            if (laughter != null) {
//                                laughter.stop();
//
//
//                            }
//                            laughter.play();
//                        }
//                    }
//                    if (((Actor) animal).getRemainingTime() == 1) {
//                        hungry.play();
//                    }
//                }
            }
        } else if (biology != null && biology.isDie()) {
            /*
            已死亡,尸体周围长出植物, 死亡时间超过一定时间则移除
             */
            if (!biology.increaseDeathTime()) {
                field.remove(biology);
            } else {
                // 尸体周围的植物可繁殖breed
                List<Biology> neighbour = field.getNeighbour(row, col, Plant.BREED_SCOPE);
                for (Biology b : neighbour) {
                    if (b instanceof Plant) {
                        Biology plant = b.breed();
                        if (plant != null) {
                            plant.setVersion(version);
                            field.placeNewBiology(b.getRow(), b.getColumn(), plant);
                        }
                    }
                }

                // 周围长出植物
                Plant cell = new Plant();
                cell.setVersion(version);
                field.placeNewBiology(row, col, cell);
            }
        }
    }

    public void stopAudio() {
        screech.stop();
        hungry.stop();
        laughter.stop();
    }
}
