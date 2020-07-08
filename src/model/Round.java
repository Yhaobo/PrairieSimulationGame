package model;

import model.entity.Location;
import model.entity.biology.Biology;
import model.entity.biology.animal.Animal;
import model.entity.biology.animal.Human;
import model.entity.biology.animal.Wolf;
import model.entity.biology.plant.Plant;
import model.interfaces.Cell;

import java.applet.Applet;
import java.applet.AudioClip;
import java.util.ArrayList;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 每一回合的处理
 */
public class Round {
    private Field field;
    private AudioClip screech;//惨叫声
    private AudioClip hungry;//肚子好饿周星驰
    private AudioClip laughter;//婴儿开心笑声

    int threadNum = Runtime.getRuntime().availableProcessors() / 2;
    private ThreadPoolExecutor threads = new ThreadPoolExecutor(2, threadNum,
            100, TimeUnit.SECONDS, new ArrayBlockingQueue<>(threadNum * 2));

    public Round(Field field) {
        this.field = field;
        try {
            screech = Applet.newAudioClip(this.getClass().getResource("/resource/惨叫声.wav"));
            hungry = Applet.newAudioClip(this.getClass().getResource("/resource/肚子好饿周星驰.wav"));
            laughter = Applet.newAudioClip(this.getClass().getResource("/resource/婴儿开心笑声.wav"));
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
        CountDownLatch countDownLatch = new CountDownLatch(threadNum);
        // 将Field按行分割为threadNum块, 每一块分配一个线程来执行
        for (int i = 0; i < threadNum; i++) {
            int finalI = i;
            threads.execute(() -> {
                int part = field.getHeight() / threadNum;
                int temp = finalI + 1;
                for (int row = finalI * part; row < (threadNum == temp ? field.getHeight() : part * temp); row++) {
                    for (int col = 0; col < field.getWidth(); col++) {
                        cellActionStrategy(row, col, version);
                        countDownLatch.countDown();
                    }
                }
            });
        }
        try {
            //保证线程都执行完之后才结束回合
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
//        for (int row = 0; row < field.getHeight(); row++) {
//            for (int col = 0; col < field.getWidth(); col++) {
//                action(row, col, version);
//            }
//        }
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
            //{ 判断包含,不包含则执行(确保一个cell一回合只能行动一次)
            if (biology instanceof Animal) {//如果是动物
                Animal animal = (Animal) biology;
                boolean flag = false;// 如果吃了就不能移动
                if (!(animal instanceof Actor)) {
                    // eat
                    if (animal instanceof Wolf) {//狼的捕食范围为周围两圈
                        Wolf wolf = (Wolf) animal;
                        ArrayList<Biology> listBiology = new ArrayList<>();
                        for (Cell an : field.WolfgetNeighbour(row, col)) {
                            if (an instanceof Biology) {
                                listBiology.add((Biology) an);
                            }
                        }
                        if (!listBiology.isEmpty()) {
                            Biology prey = wolf.eat(listBiology);
                            if (prey != null) {
                                if (prey instanceof Human) {
                                    screech.play();
                                }
                                field.replace(wolf, prey);
                                flag = true;
                            }
                        }
                    } else {//其他动物捕食范围为周围一圈
                        ArrayList<Biology> listBiology = new ArrayList<>();
                        for (Cell an : field.getNeighbour(row, col)) {
                            if (an instanceof Biology) {
                                listBiology.add((Biology) an);
                            }
                        }
                        if (!listBiology.isEmpty()) {
                            Biology prey = animal.eat(listBiology);
                            if (prey != null) {
                                field.replace(animal, prey);
                                flag = true;
                            }
                        }
                    }
                    // breed
                    Cell baby = animal.breed();
                    if (baby != null) {
                        field.placeRandomAdj(row, col, baby);
                    }
                    // move
                    if (!flag) {
                        Location loc = animal.move(animal.lookAround(field));
                        if (loc != null) {
                            field.move(row, col, loc);
                        }
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
            } else {//如果是植物
                // breed
                Cell baby = biology.breed();
                if (baby != null) {
                    field.placeRandomAdj(row, col, baby);
                }
            }

        } else if (biology != null && !biology.isAlive()) {
            // 死亡
            field.remove(row, col);
            // 尸体上长出植物
            Plant cell = new Plant(Cell.ONE_YEAR_DAYS);
            cell.setVersion(version);
            field.place(row, col, cell);
        }
    }

    public void stopAudio() {
        screech.stop();
        hungry.stop();
        laughter.stop();
    }
}
