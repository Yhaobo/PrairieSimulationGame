package model;

import model.entity.Location;
import model.entity.biology.Biology;
import model.entity.biology.animal.Animal;
import model.entity.biology.animal.Human;
import model.entity.biology.animal.Wolf;
import model.interfaces.Cell;

import java.applet.Applet;
import java.applet.AudioClip;
import java.util.ArrayList;

/**
 * 每一回合的处理
 */
public class Round {
    private Field field;
    private AudioClip screech;//惨叫声
    private AudioClip hungry;//肚子好饿周星驰
    private AudioClip laughter;//婴儿开心笑声

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

    public void oneFrame(int version) {
//        HashSet<Cell> isContains = new HashSet<>();
        for (int row = 0; row < field.getHeight(); row++) {
            for (int col = 0; col < field.getWidth(); col++) {
                action(row, col,version);
            }
        }
    }

    private void action(int row, int col,int version) {
        Biology biology = (Biology)field.getCell(row, col);
        if (biology != null && biology.isAlive() && biology.compareVersion(version)) {
            //{ 判断包含,不包含则执行(确保一个cell一回合只能行动一次)
            biology.grow();
            if (biology instanceof Animal) {//如果是动物
                Animal animal = (Animal) biology;
                boolean flag = false;// 如果吃了就不能移动
                if (!(animal instanceof Actor)) {
                    // eat
                    if (animal instanceof Wolf) {//狼的捕食范围为周围两圈
                        Wolf wolf = (Wolf) animal;
                        if (!wolf.huntHumanFlag) {
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
                                        wolf.huntHumanFlag = true;
                                        if (prey instanceof Actor) {
                                            screech.play();
                                        }
                                    }
                                    field.instead(wolf, prey);
                                    flag = true;
                                }
                            }
                        } else {
                            wolf.huntHumanFlag = false;
                            flag = true;
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
                                field.instead(animal, prey);
                                flag = true;
                            }
                        }
                    }
                    // breed
                    Cell baby = animal.breed();
                    if (baby != null) {
                        field.placeRandomAdj(row, col, baby);
//                        isContains.add(baby);
                    }
                    // move
                    if (!flag) {
                        Location loc = animal.move(field.getFreeNeighbour(row, col));
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
//                    isContains.add(baby);
                }
            }

        } else if (biology != null &&!biology.isAlive()) {
            field.remove(row, col);
        }
    }

    public void stopAudio() {
        screech.stop();
        hungry.stop();
        laughter.stop();
    }
}
