package model.entity.biology;

import model.entity.Location;
import model.interfaces.Cell;

import java.io.Serializable;

/**
 * 生物
 */
public abstract class Biology implements Serializable, Cell {
    private boolean isAlive = true;
    private Location location;
    protected int version;

    /**
     * 最大寿命
     */
    protected final int maxLifetime;
    /**
     * 最大存活时间(动态,由吃东西增加)
     */
    protected int maxAliveTime;
    /**
     * 成熟时间
     */
    protected int adultTime;
    /**
     * 存活时间
     */
    protected int aliveTime;

    public Biology(int aliveTime, int maxAliveTime, int adultTime, int maxLifetime) {
        this.maxAliveTime = maxAliveTime;
        this.adultTime = adultTime;
        this.maxLifetime = maxLifetime;
        this.aliveTime = aliveTime;
    }

    @Override
    public synchronized boolean compareVersion(int newVersion) {
        if (newVersion - 1 == version) {
            version = newVersion;
            return true;
        } else {
            return false;
        }
    }

    @Override
    public double getRemainTimePercent() {
        int remainTime = maxAliveTime - aliveTime;
        if (remainTime <= REMAIN_TIME_WARNING) {
            return (double) remainTime / REMAIN_TIME_WARNING;
        }
        return 1;
    }

    @Override
    public void setLocation(int row, int column) {
        location = new Location(row, column);
    }

    @Override
    public Location getLocation() {
        return location;
    }

    /**
     * 繁殖
     *
     * @return 新生命
     */
    public abstract Biology breed();

    /**
     * 生长
     *
     * @return 如果死亡返回false, 活着则返回true
     */
    public boolean grow() {
        if (maxLifetime <= 0) {
            // 主角寿命无限
            return true;
        }
        if (++aliveTime > maxAliveTime || aliveTime > maxLifetime) {
            die();
            return false;
        }
        return true;
    }

    public boolean isAlive() {
        return isAlive;
    }

    public abstract boolean isReproducible();

    public void die() {
        isAlive = false;
    }

    public int getRow() {
        return location.getRow();
    }

    public int getColumn() {
        return location.getColumn();
    }

}
