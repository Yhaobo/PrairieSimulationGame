package model.entity.biology.animal;

import model.Field;
import model.entity.Location;
import model.entity.biology.Biology;
import model.entity.biology.plant.Plant;
import model.interfaces.Cell;
import util.ConstantNum;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class Human extends Animal {
    private static final double PROBABILITY = 1;

    private static final int SENSE_RADIUS = 5;
    /**
     * 感官范围内的相对位置
     */
    private static final List<Location> RELATIVE_SENSE_SCOPE = Field.getRelativeLocationList(SENSE_RADIUS);

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
    public int getSenseRadius() {
        return SENSE_RADIUS;
    }

    @Override
    public Location lookAround(Field field) {
        List<Location> freeNeighbour = field.getFreeAdjacentLocations(getRow(), getColumn(), 1);
        if (freeNeighbour.isEmpty()) {
            return null;
        }
        List<Location> locations = new ArrayList<>(2);
        List<Plant> plants = new ArrayList<>();
        // 直线位置(不需要绕障碍物)
        Location directLocation;
        for (Location relativeLocation : RELATIVE_SENSE_SCOPE) {
            int row = getRow() + relativeLocation.getRow();
            int col = getColumn() + relativeLocation.getColumn();
            if (row > -1 && row < field.getHeight() && col > -1 && col < field.getWidth()) {
                Cell cell = field.getCell(row, col);
                if (cell != null) {
                    // 狼和羊的优先级一样 (人为羊死,鸟为食亡)
                    if (cell instanceof Wolf) {
                        // 远离狼
                        directLocation = Animal.away(relativeLocation, this, freeNeighbour, locations);
                        if (directLocation != null) {
                            return directLocation;
                        } else if (!locations.isEmpty()) {
                            return locations.get((int) (Math.random() * locations.size()));
                        }
                    } else if (cell instanceof Sheep) {
                        // 靠近羊
                        directLocation = Animal.near(relativeLocation, this, freeNeighbour, locations);
                        if (directLocation != null) {
                            return directLocation;
                        } else if (!locations.isEmpty()) {
                            return locations.get((int) (Math.random() * locations.size()));
                        }
                    } else if (cell instanceof Plant) {
                        plants.add((Plant) cell);
                    }
                }
            }
        }

        if (!plants.isEmpty()) {
            // 如果只有植物,则选择最近的靠近
            Plant firstPlant = plants.get(0);
            directLocation = Animal.near(new Location(firstPlant.getRow() - getRow(), firstPlant.getColumn() - getColumn()), this, freeNeighbour, locations);
            if (directLocation != null) {
                return directLocation;
            } else if (!locations.isEmpty()) {
                return locations.get((int) (Math.random() * locations.size()));
            }
        }
        if (!freeNeighbour.isEmpty()) {
            // 随机移动
            return freeNeighbour.get((int) (Math.random() * freeNeighbour.size()));
        }
        return null;
    }

    @Override
    public List<Location> getSenseScopeRelativeLocation() {
        return RELATIVE_SENSE_SCOPE;
    }
}
