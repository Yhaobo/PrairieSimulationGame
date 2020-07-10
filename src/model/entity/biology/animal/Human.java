package model.entity.biology.animal;

import model.Field;
import util.ConstantNum;
import util.MyUtils;
import model.entity.Location;
import model.entity.biology.Biology;
import model.entity.biology.plant.Plant;
import model.interfaces.Cell;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class Human extends Animal {
    private static final double PROBABILITY = 1;
//    private boolean hero = false;
    /**
     * 感官范围内的相对位置
     */
    private static final List<Location> RELATIVE_SENSE_SCOPE = MyUtils.generateSenseSope(5);

    public Human() {
        this(0);
    }

    public Human(int aliveTime, int maxLifetime) {
        super(aliveTime, aliveTime + 5, 16 * ConstantNum.ONE_YEAR_DAYS.value, maxLifetime);
    }

    public Human(int aliveTime) {
        this(aliveTime, 60 * ConstantNum.ONE_YEAR_DAYS.value);
    }

    @Override
    public boolean isReproducible() {
        return aliveTime >= adultTime && (aliveTime % ConstantNum.ONE_YEAR_DAYS.value == 0);
    }

    @Override
    public void draw(Graphics g, int x, int y, int size) {
        if (isAlive()) {
            g.setColor(new Color(0, 0, 255, (int) (getRemainTimePercent() * 255)));
        } else {
            g.setColor(Color.BLACK);
        }
        g.fill3DRect(x, y, size, size, true);
    }

    @Override
    public Biology breed() {
        Human ret = null;
        if (isReproducible() && Math.random() < PROBABILITY) {
            ret = new Human();
            ret.version = new AtomicInteger(this.version.get());
        }
        return ret;
    }

    @Override
    public Biology eat(List<Biology> neighbour) {
        Biology ret = null;
//        ArrayList<Biology> wolfs = new ArrayList<>();
//        ArrayList<Biology> sheep = new ArrayList<>();
        ArrayList<Biology> plants = new ArrayList<>();
        for (Biology i : neighbour) {
            if (i instanceof Wolf) {
                maxAliveTime += ConstantNum.ONE_YEAR_DAYS.value / 12 + i.getRemainTime();
                adultTime = 0;
                return i;
            } else if (i instanceof Sheep) {
                maxAliveTime += ConstantNum.ONE_YEAR_DAYS.value / 12 / 2 + i.getRemainTime();
                return i;
            } else if (i instanceof Plant) {
                plants.add(i);
            }

        }
        if (!plants.isEmpty()) {
            //随机获取一只
            ret = plants.get((int) (Math.random() * plants.size()));
            maxAliveTime += 1;
        }
        return ret;
    }

    @Override
    public Location move(Location location) {
        Location ret = null;
        if (location != null) {
            ret = location;
        }
        return ret;
    }

    @Override
    public Location lookAround(Field field) {
        List<Location> freeNeighbour = field.getFreeAdjacentLocation(getRow(), getColumn(), 1);
        List<Location> locations = new ArrayList<>(2);
        List<Plant> plants = new ArrayList<>();
        if (freeNeighbour.isEmpty()) {
            return null;
        }
        for (Location relativeLocation : RELATIVE_SENSE_SCOPE) {
            int row = getRow() + relativeLocation.getRow();
            int col = getColumn() + relativeLocation.getColumn();
            if (row > -1 && row < field.getHeight() && col > -1 && col < field.getWidth()) {
                Cell cell = field.getCell(row, col);
                if (cell != null) {
                    if (cell instanceof Wolf) {
                        // 远离狼
                        Location nearLocation = Animal.away(relativeLocation, this, freeNeighbour, locations);
                        if (nearLocation != null) {
                            return nearLocation;
                        } else if (!locations.isEmpty()) {
                            break;
                        }
                    } else if (cell instanceof Sheep) {
                        // 靠近羊
                        Location nearLocation = Animal.near(relativeLocation, this, freeNeighbour, locations);
                        if (nearLocation != null) {
                            return nearLocation;
                        }else if (!locations.isEmpty()) {
                            break;
                        }
                    } else if (cell instanceof Plant) {
                        plants.add((Plant) cell);
                    }
                }
            }
        }
        // 如果只有植物,则选择最近的靠近
        if (!plants.isEmpty()) {
            Plant firstPlant = plants.get(0);
            Location nearLocation = Animal.near(new Location(firstPlant.getRow() - getRow(), firstPlant.getColumn() - getColumn()), this, freeNeighbour, locations);
            if (nearLocation != null) {
                return nearLocation;
            }
        }
        if (!locations.isEmpty()) {
            return locations.get(0);
        }
        if (!freeNeighbour.isEmpty()) {
            return freeNeighbour.get((int) (Math.random() * freeNeighbour.size()));
        }
        return null;
    }
}
