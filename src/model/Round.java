package model;

import controller.PrairieStory;
import model.entity.Location;
import model.entity.biology.Biology;
import model.entity.biology.animal.Animal;
import model.entity.biology.animal.HumanPlayer;
import model.entity.biology.animal.Wolf;
import model.entity.biology.plant.Plant;
import model.interfaces.Cell;
import model.interfaces.Player;
import view.View;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * 每一回合的处理
 */
public class Round {
    private final Field field;
    private final View view;

    /**
     * 可用线程数
     */
    private final int availableThreadTotal = PrairieStory.threadPool.getMaximumPoolSize();
    /**
     * 根据可用线程数来分配任务量(行);
     */
    private final int rowPart;

    public Round(Field field, View view) {
        this.field = field;
        this.view = view;
        this.rowPart = field.getHeight() / availableThreadTotal;
//        this.cyclicBarrier = new CyclicBarrier(availableThreadTotal);

    }

    /**
     * 每一回合的处理
     *
     * @param version 最新版本号
     */
    public void oneRoundByLoop(int version) {
        if (availableThreadTotal > 1) {
            /*
            并发执行
            */
            CountDownLatch countDownLatch = new CountDownLatch(availableThreadTotal);
            // 将Field按行分割为availableThreadTotal个块, 每一块分配一个线程来执行
            for (int thread = 0; thread < availableThreadTotal; thread++) {
                int i = thread;
                PrairieStory.threadPool.execute(() -> {
                    int iPlusOne = i + 1;
                    int startRow = i * rowPart;
                    // 同步点(一般为任务的三分之一和三分之二两个位置)
                    int syncPoint = startRow + (rowPart / 3);
                    for (int row = startRow; row < (availableThreadTotal == iPlusOne ? field.getHeight() : rowPart * iPlusOne); row++) {
                        for (int col = 0; col < field.getWidth(); col++) {
                            if (row == syncPoint) {
                                Thread.yield();
                            /*
                            下面代码的功能已被Thread.yield()完美代替(性能翻倍)
                             */
                            }
                            cellActionStrategy(row, col, version);
                        }
                    }
                    countDownLatch.countDown();
                });
            }
            try {
                // main线程等待任务线程都执行完之后才继续执行
                countDownLatch.await();
                // 每回合重置
//            cyclicBarrier.reset();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } else {
            /*
            串行执行
            */
            for (int row = 0; row < field.getHeight(); row++) {
                for (int col = 0; col < field.getWidth(); col++) {
                    cellActionStrategy(row, col, version);
                }
            }
        }
    }

    /**
     * 每一回合的处理
     *
     * @param version 最新版本号
     */
    public void oneRoundByPlayer(int version) {
        if (availableThreadTotal > 1) {
            /*
            并发执行
            */
            // 将Field按行分割为availableThreadTotal个块, 每一块分配一个线程来执行
            for (int thread = 0; thread < availableThreadTotal; thread++) {
                int i = thread;
                PrairieStory.threadPool.execute(() -> {
                    int iPlusOne = i + 1;
                    int startRow = i * rowPart;
                    // 同步点(一般为任务的三分之一和三分之二两个位置)
                    int syncPoint = startRow + (rowPart / 3);
                    for (int row = startRow; row < (availableThreadTotal == iPlusOne ? field.getHeight() : rowPart * iPlusOne); row++) {
                        for (int col = 0; col < field.getWidth(); col++) {
                            if (row == syncPoint) {
                                Thread.yield();
                            }
                            cellActionStrategy(row, col, version);
                        }
                    }
                });
            }
        } else {
            /*
            串行执行
            */
            for (int row = 0; row < field.getHeight(); row++) {
                for (int col = 0; col < field.getWidth(); col++) {
                    cellActionStrategy(row, col, version);
                }
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
                if (!(animal instanceof Player)) {
                    /*
                    move
                    注意: 移动之后位置改变了,不能用row和col来表示动物位置
                     */
                    Location loc;
                    if (animal.isNoNeedEat()) {
                        loc = animal.move(animal.awayPlant(field, animal.getSenseScopeRelativeLocation()));
                    } else {
                        loc = animal.move(animal.lookAround(field));
                    }
                    field.move(animal, loc);

                    /*
                    eat
                     */
                    if (animal instanceof Wolf) {
                        //狼的捕食范围为周围两圈
                        Wolf wolf = (Wolf) animal;
                        List<Biology> listBiology = field.getNeighbour(animal.getRow(), animal.getColumn(), 2);
                        if (!listBiology.isEmpty()) {
                            Biology prey = wolf.eat(listBiology);
                            if (prey != null) {
                                if (prey instanceof HumanPlayer) {
                                    GameAudio.getScreech().play();
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
                                    GameAudio.getWolfAudio().play();
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
                } else {
                    // player
                    Player player = (Player) animal;
                    // breed
                    if (player.isBreedFlag()) {
                        player.setBreedFlag(false);
                        Cell baby = animal.breed();
                        if (baby != null) {
                            field.placeNewBiology(row, col, baby);
                            GameAudio.getLaughter().play();
                        }
                    }
                }
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
                field.placeNewBiology(biology.getRow(), biology.getColumn(), cell);
            }
        }
    }


}
