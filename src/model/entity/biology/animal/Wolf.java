package model.entity.biology.animal;

import model.Field;
import model.MyUtils;
import model.entity.Location;
import model.entity.biology.Biology;
import model.interfaces.Cell;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class Wolf extends Animal {
    /**
     * 视野范围
     */
    private static final List<Location> RELATIVE_SENSE_SCOPE = MyUtils.generateVIEW_SCOPE(10);

    public Wolf() {
        this(0);
    }

    public Wolf(int aliveTime) {
        super(aliveTime, aliveTime + 50, 3 * ONE_YEAR_DAYS, 20 * ONE_YEAR_DAYS);
    }

    @Override
    public boolean isReproducible() {
        return aliveTime >= adultTime && (aliveTime % ONE_YEAR_DAYS / 5 == 0);
    }

    @Override
    public void draw(Graphics g, int x, int y, int size) {
        g.setColor(new Color(255, 0, 0, (int) (getRemainTimePercent() * 255)));
        g.fill3DRect(x, y, size, (int) (size / 1.2), true);
//        g.fillArc(x,y,size,size,0,360);
//        int[] xPoints = new int[]{x + size / 2, x, x + size};
//        int[] yPoints = new int[]{y, y + size, y + size};
//        g.fillPolygon(xPoints,yPoints,3);
    }

    @Override
    public Biology breed() {
        Wolf ret = null;
        if (isReproducible()) {
            ret = new Wolf();
            ret.version = this.version;
        }
        return ret;
    }

    @Override
    public Biology eat(ArrayList<Biology> neighbour) {
        Biology ret = null;
        ArrayList<Biology> sheeps = new ArrayList<>();
        ArrayList<Human> humans = new ArrayList<>();
        for (Biology bio : neighbour) {
            if (bio instanceof Sheep) {
                sheeps.add(bio);
            }
            if (bio instanceof Human) {
                humans.add((Human) bio);
            }
        }
        if (!sheeps.isEmpty()) {
            ret = sheeps.get((int) (Math.random() * sheeps.size()));
            maxAliveTime += ONE_YEAR_DAYS / 12 / 2;
        } else if (!humans.isEmpty()) {
            ret = humans.get((int) (Math.random() * humans.size()));
            maxAliveTime += ONE_YEAR_DAYS / 12 / 2;
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
        List<Location> freeNeighbour = field.getFreeNeighbour(getRow(), getColumn());
        List<Location> locations = new ArrayList<>(2);
        if (freeNeighbour.isEmpty()) {
            return null;
        }
        for (Location relativeLocation : RELATIVE_SENSE_SCOPE) {
            int row = getRow() + relativeLocation.getRow();
            int col = getColumn() + relativeLocation.getColumn();
            if (row > -1 && row < field.getHeight() && col > -1 && col < field.getWidth()) {
                Cell cell = field.getCell(row, col);
                if (cell != null) {
                    if (cell instanceof Sheep || cell instanceof Human) {
                        // 靠近羊和人
                        Location nearLocation = Animal.near(relativeLocation, this, freeNeighbour, locations);
                        if (nearLocation != null) {
                            return nearLocation;
                        }
                    }
                }
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
