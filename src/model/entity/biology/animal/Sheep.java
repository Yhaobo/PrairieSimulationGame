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

public class Sheep extends Animal {
    private static final int SENSE_RADIUS = 7;
    /**
     * 感官范围
     */
    private static final List<Location> RELATIVE_SENSE_SCOPE = MyUtils.generateSenseSope(SENSE_RADIUS);

    public Sheep() {
        this(0);
    }

    public Sheep(int aliveTime) {
        super(aliveTime, aliveTime + 5, 2 * ConstantNum.ONE_YEAR_DAYS.value, 12 * ConstantNum.ONE_YEAR_DAYS.value);
    }

    @Override
    public boolean isReproducible() {
        return aliveTime >= adultTime && (aliveTime % (ConstantNum.ONE_YEAR_DAYS.value / 12) == 0);
    }

    @Override
    public void draw(Graphics g, int x, int y, int size) {
        if (isAlive()) {
            g.setColor(new Color(250, 250, 250, (int) (getRemainTimePercent() * 255)));
        } else {
            g.setColor(Color.BLACK);
        }
        g.fillOval(x, y, size, size);
    }

    @Override
    public Biology breed() {
        Sheep ret = null;
        if (isReproducible()) {
            ret = new Sheep();
            ret.version = new AtomicInteger(this.version.get());
        }
        return ret;
    }

    @Override
    public Biology eat(List<Biology> neighbour) {
        Biology ret = null;
        ArrayList<Plant> list = new ArrayList<>();
        for (Biology bio : neighbour) {
            if (bio instanceof Plant) {
                list.add((Plant) bio);
            }
        }
        if (!list.isEmpty()) {
            ret = list.get((int) (Math.random() * list.size()));
            maxAliveTime += 2;
        }
        return ret;
    }

    @Override
    public Location move(Location location) {
        Location ret = null;
        if (location != null && Math.random() < 0.75) {
            ret = location;
        }
        return ret;
    }

    @Override
    public Location lookAround(Field field) {
        List<Location> freeNeighbour = field.getFreeAdjacentLocations(getRow(), getColumn(), 1);
        if (freeNeighbour == null) {
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
                    if (cell instanceof Wolf || cell instanceof Human) {
                        // 优先远离狼和人 (胆小)
                        directLocation = Animal.away(relativeLocation, this, freeNeighbour, locations);
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
    public int getSenseRadius() {
        return SENSE_RADIUS;
    }

    @Override
    public List<Location> getSenseScopeRelativeLocation() {
        return RELATIVE_SENSE_SCOPE;
    }
}
