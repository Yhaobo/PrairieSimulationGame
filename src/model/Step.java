package model;

import model.biology.Biology;
import model.biology.animal.Animal;
import model.biology.animal.Human;
import model.biology.animal.Wolf;

import java.applet.Applet;
import java.applet.AudioClip;
import java.util.ArrayList;
import java.util.HashSet;

public class Step {
    private Field field;
    private AudioClip screech;//惨叫声
    private AudioClip hungry;//肚子好饿周星驰
    private AudioClip laughter;//婴儿开心笑声

    public Step(Field field) {
        this.field = field;
        try {
            screech = Applet.newAudioClip(this.getClass().getResource("/resource/惨叫声.wav"));
            hungry = Applet.newAudioClip(this.getClass().getResource("/resource/肚子好饿周星驰.wav"));
            laughter = Applet.newAudioClip(this.getClass().getResource("/resource/婴儿开心笑声.wav"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void handle() {
        HashSet<Cell> isContains = new HashSet<>();
        for (int row = 0; row < field.getHeight(); row++) {
            for (int col = 0; col < field.getWidth(); col++) {
                impl(row, col, isContains);
                Thread.yield();
            }
        }
    }

    private void impl(int row, int col, HashSet<Cell> isContains) {
        Biology biology = (Biology)field.get(row, col);
        if (biology != null && biology.isAlive() && isContains.add(biology)) {//{ 判断包含,不包含则执行(确保一个cell一回合只能行动一次)
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
                                Biology prey = animal.feed(listBiology);
                                if (prey != null) {
                                    if (prey instanceof Human) {
                                        wolf.huntHumanFlag = true;
                                        if (prey instanceof Actor) {
                                            screech.play();
                                        }
                                    }
                                    field.eat(animal, prey);
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
                            Biology prey = animal.feed(listBiology);
                            if (prey != null) {
                                field.eat(animal, prey);
                                flag = true;
                            }
                        }
                    }
                    // breed
                    Cell baby = animal.breed();
                    if (baby != null) {
                        field.placeRandomAdj(row, col, baby);
                        isContains.add(baby);
                    }
                    // move
                    if (!flag) {
                        Location loc = animal.move(field.getFreeNeighbour(row, col));
                        if (loc != null) {
                            field.move(row, col, loc);
                        }
                    }
                } else {//actor
                    // breed
                    Cell baby = animal.breed();
                    if (baby != null) {
                        field.placeRandomAdj(row, col, baby);
                        if (isContains.add(baby)) {
                            if (laughter != null) {
                                laughter.stop();
                            }
                            laughter.play();
                        }
                    }
                    if (((Actor) animal).getTime() == 1) {
                        hungry.play();
                    }
                }
            } else {//如果是植物
                // breed
                Cell baby = biology.breed();
                if (baby != null) {
                    field.placeRandomAdj(row, col, baby);
                    isContains.add(baby);
                }
            }
            biology.grow();
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
