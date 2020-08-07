package model.entity.biology.animal;

import model.Field;
import model.entity.Location;
import model.entity.biology.Biology;
import model.interfaces.Cell;
import util.ConstantNum;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class Wolf extends Animal {
    private static final int SENSE_RADIUS = 10;
    /**
     * 感官范围
     */
    private static final List<Location> RELATIVE_SENSE_SCOPE = Field.getRelativeLocationList(SENSE_RADIUS);

    public Wolf() {
        this(0);
    }

    public Wolf(int aliveTime) {
        super(aliveTime, aliveTime + 5, 3 * ConstantNum.ONE_YEAR_DAYS.value, 20 * ConstantNum.ONE_YEAR_DAYS.value);
    }

    @Override
    public boolean isReproducible() {
        return aliveTime >= adultTime && (aliveTime % (ConstantNum.ONE_YEAR_DAYS.value / 6) == 0);
    }

    @Override
    public void draw(Graphics g, int x, int y, int size) {
        if (isAlive()) {
            g.setColor(new Color(255, 0, 0, (int) (getRemainTimePercent() * 255)));
        } else {
            g.setColor(Color.BLACK);
        }
        g.fill3DRect(x, y, size, size, true);
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
            ret.version = new AtomicInteger(this.version.get());
        }
        return ret;
    }

    @Override
    public Biology eat(List<Biology> neighbour) {
//        ArrayList<Biology> sheeps = new ArrayList<>();
//        ArrayList<Human> humans = new ArrayList<>();
        for (Biology prey : neighbour) {
            if (prey instanceof Sheep) {
                maxAliveTime += ConstantNum.ONE_YEAR_DAYS.value / 12 / 2 + prey.getRemainTime() / 2;
                return prey;
            }
            if (prey instanceof Human) {
                maxAliveTime += ConstantNum.ONE_YEAR_DAYS.value / 12 / 2 + prey.getRemainTime();
                return prey;
            }
        }
//        if (!sheeps.isEmpty()) {
//            prey = sheeps.get((int) (Math.random() * sheeps.size()));
//            maxAliveTime += ConstantNum.ONE_YEAR_DAYS.value / 12 / 2 + prey.getRemainTime() / 2;
//        } else if (!humans.isEmpty()) {
//            prey = humans.get((int) (Math.random() * humans.size()));
//            maxAliveTime += ConstantNum.ONE_YEAR_DAYS.value / 12 / 2 + prey.getRemainTime();
//        }
        return null;
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
        List<Location> freeNeighbour = field.getFreeAdjacentLocations(getRow(), getColumn(), 1);
        if (freeNeighbour.isEmpty()) {
            return null;
        }
        List<Location> locations = new ArrayList<>(2);
        for (Location relativeLocation : RELATIVE_SENSE_SCOPE) {
            int row = getRow() + relativeLocation.getRow();
            int col = getColumn() + relativeLocation.getColumn();
            if (row > -1 && row < field.getHeight() && col > -1 && col < field.getWidth()) {
                Cell cell = field.getCell(row, col);
                if (cell != null) {
                    if (cell instanceof Sheep || cell instanceof Human) {
                        // 靠近羊或人
                        Location directLocation = Animal.near(relativeLocation, this, freeNeighbour, locations);
                        if (directLocation != null) {
                            return directLocation;
                        } else if (!locations.isEmpty()) {
                            return locations.get((int) (Math.random() * locations.size()));
                        }
                    }
                }
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
