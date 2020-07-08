package model.entity.biology.animal;

import model.Field;
import model.MyUtils;
import model.entity.Location;
import model.entity.biology.Biology;
import model.entity.biology.plant.Plant;
import model.interfaces.Cell;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class Sheep extends Animal {
    /**
     * 视野范围
     */
    private static final List<Location> RELATIVE_SENSE_SCOPE = MyUtils.generateVIEW_SCOPE(5);

    public Sheep() {
        this(0);
    }

    public Sheep(int aliveTime) {
        super(aliveTime, aliveTime + 15, 2 * ONE_YEAR_DAYS, 12 * ONE_YEAR_DAYS);
    }

    @Override
    public boolean isReproducible() {
        return aliveTime >= adultTime && (aliveTime % ONE_YEAR_DAYS / 10 == 0);
    }

    @Override
    public void draw(Graphics g, int x, int y, int size) {
        g.setColor(new Color(250, 250, 250, (int) (getRemainTimePercent() * 255)));
//        g.fillRect(x, y, size, size);
        g.fillOval(x, y, size, size);

    }

    @Override
    public Biology breed() {
        Sheep ret = null;
        if (isReproducible()) {
            ret = new Sheep();
            ret.version = this.version;
        }
        return ret;
    }

    @Override
    public Biology eat(ArrayList<Biology> neighbour) {
        Biology ret = null;
        ArrayList<Plant> list = new ArrayList<>();
        for (Biology bio : neighbour) {
            if (bio instanceof Plant) {
                list.add((Plant) bio);
            }
        }
        if (!list.isEmpty()) {
            ret = list.get((int) (Math.random() * list.size()));
            maxAliveTime += ONE_YEAR_DAYS / 12 / 6;
        }
        return ret;
    }

    @Override
    public Location move(Location location) {
        Location ret = null;
        if (location != null && Math.random() < 0.8) {
            ret = location;
        }
        return ret;
    }

    @Override
    public Location lookAround(Field field) {
        List<Location> freeNeighbour = field.getFreeNeighbour(getRow(), getColumn());
        List<Location> locations = new ArrayList<>(2);
        List<Plant> plants = new ArrayList<>();
        for (Location relativeLocation : RELATIVE_SENSE_SCOPE) {
            int row = getRow() + relativeLocation.getRow();
            int col = getColumn() + relativeLocation.getColumn();
            if (row > -1 && row < field.getHeight() && col > -1 && col < field.getWidth()) {
                Cell cell = field.getCell(row, col);
                if (cell != null) {
                    if (cell instanceof Wolf || cell instanceof Human) {
                        // 远离狼和人
                        Location nearLocation = Animal.away(relativeLocation, this, freeNeighbour, locations);
                        if (nearLocation != null) {
                            return nearLocation;
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
