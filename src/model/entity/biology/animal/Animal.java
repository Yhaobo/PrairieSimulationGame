package model.entity.biology.animal;

import model.Field;
import model.entity.Location;
import model.entity.biology.Biology;
import model.entity.biology.plant.Plant;
import model.interfaces.Cell;

import java.util.ArrayList;
import java.util.List;

/**
 * 动物
 *
 * @author Yhaobo
 */
public abstract class Animal extends Biology {

    public Animal(int aliveTime, int maxAliveTime, int adultAge, int maxLifetime) {
        super(aliveTime, maxAliveTime, adultAge, maxLifetime);
    }

    public abstract Biology eat(List<Biology> neighbour);

    public abstract Location move(Location location);

    /**
     * 当maxAliveTime大于最大寿命时, 就不再需要吃东西了
     *
     * @return
     */
    public boolean isNoNeedEat() {
        return maxAliveTime > maxLifetime;
    }

    /**
     * 远离植物, 否则不动
     *
     * @param field
     * @param senseScope 感官范围
     * @return
     */
    public Location awayPlant(Field field, List<Location> senseScope) {
        List<Location> freeAdjacentLocations = field.getFreeAdjacentLocations(this.getRow(), this.getColumn(), getSenseRadius());
        if (freeAdjacentLocations == null) {
            return null;
        }
        ArrayList<Location> locations = new ArrayList<>();
        for (Location relativeLoc : senseScope) {
            Cell cell = field.getCell(this.getRow() + relativeLoc.getRow(), this.getColumn() + relativeLoc.getColumn());
            if (cell instanceof Plant) {
                Location awayLocation = away(relativeLoc, this, freeAdjacentLocations, locations);
                if (awayLocation != null) {
                    return awayLocation;
                } else if (!locations.isEmpty()) {
                    return locations.get((int) (Math.random() * locations.size()));
                }
            }
        }
        return null;
    }

    /**
     * 返回感官半径
     *
     * @return 返回
     */
    public abstract int getSenseRadius();

    /**
     * 环顾视野之内,远离危险,靠近食物
     *
     * @param field
     * @return 返回最近一圈离意图最近的空位置(准备移动的位置)
     */
    public abstract Location lookAround(Field field);

    /**
     * 靠近目标
     *
     * @param relativeLocation
     * @param protagonist      主角
     * @param freeNeighbours
     * @param locations
     * @return 返回靠近目标的直接相对位置, 没有则返回null
     */
    protected static Location near(Location relativeLocation, Animal protagonist, List<Location> freeNeighbours, List<Location> locations) {
        int relativeRow = 0, relativeCol = 0;
        if (relativeLocation.getRow() < 0) {
            relativeRow = -1;
        } else if (relativeLocation.getRow() > 0) {
            relativeRow = 1;
        }
        if (relativeLocation.getColumn() < 0) {
            relativeCol = -1;
        } else if (relativeLocation.getColumn() > 0) {
            relativeCol = 1;
        }
        for (Location freeLocation : freeNeighbours) {
            int sum = Math.abs((freeLocation.getRow() - protagonist.getRow() - relativeRow))
                    + Math.abs(freeLocation.getColumn() - protagonist.getColumn() - relativeCol);
            if (sum == 0) {
                // 直接靠近
                return freeLocation;
            } else if (sum == 1) {
                // 迂回靠近
                locations.add(freeLocation);
            }
        }
        return null;
    }

    /**
     * 远离危险
     *
     * @param relativeLocation
     * @param protagonist      主角
     * @param freeNeighbour
     * @param locations
     * @return 返回远离目标的直接相对位置, 没有则返回null
     */
    protected static Location away(Location relativeLocation, Animal protagonist, List<Location> freeNeighbour, List<Location> locations) {
        int relativeRow = 0, relativeCol = 0;
        if (relativeLocation.getRow() < 0) {
            relativeRow = 1;
        } else if (relativeLocation.getRow() > 0) {
            relativeRow = -1;
        }
        if (relativeLocation.getColumn() < 0) {
            relativeCol = 1;
        } else if (relativeLocation.getColumn() > 0) {
            relativeCol = -1;
        }
        for (Location freeLocation : freeNeighbour) {
            int sum = Math.abs(freeLocation.getRow() - protagonist.getRow() - relativeRow) + Math.abs(freeLocation.getColumn() - protagonist.getColumn() - relativeCol);
            if (sum == 0) {
                return freeLocation;
            } else if (sum == 1) {
                locations.add(freeLocation);
            }
        }
        return null;
    }

    /**
     * 得到感官范围内的所有相对位置
     *
     * @return
     */
    public abstract List<Location> getSenseScopeRelativeLocation();
}
