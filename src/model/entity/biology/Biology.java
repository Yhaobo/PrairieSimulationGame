package model.entity.biology;

import model.entity.Location;
import model.entity.biology.plant.Plant;
import model.interfaces.Cell;
import util.ConstantNum;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * 生物
 */
public abstract class Biology implements Cell {
    private boolean isAlive = true;
    private Location location;
    protected AtomicInteger version = new AtomicInteger(0);

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
    protected int deathTime;

    public Biology(int aliveTime, int maxAliveTime, int adultTime, int maxLifetime) {
        this.maxAliveTime = maxAliveTime;
        this.adultTime = adultTime;
        this.maxLifetime = maxLifetime;
        this.aliveTime = aliveTime;
    }

    @Override
    public boolean compareVersion(int newVersion) {
        if (version.compareAndSet(newVersion - 1, newVersion)) {
            return true;
        } else {
            if (version.get() != newVersion) {
                System.out.println("版本控制异常,version=" + version + ",newVersion=" + newVersion + "\t" + getClass().getSimpleName());
                version.set(newVersion);
            }
            return false;
        }
    }

    @Override
    public double getRemainTimePercent() {
        int remainTime = getRemainTime() + 1;
        if (remainTime <= ConstantNum.REMAIN_TIME_WARNING.value) {
            return (double) (Math.max(remainTime, 0)) / ConstantNum.REMAIN_TIME_WARNING.value;
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
        if (this.isDie()) {
            return false;
        }
        if (this instanceof Plant) {
            aliveTime++;
            // 植物寿命无限
            return true;
        }
        if (aliveTime++ > maxAliveTime || aliveTime > maxLifetime) {
            die();
            return false;
        }
        return true;
    }

    public boolean isAlive() {
        return isAlive;
    }

    public void setAlive(boolean alive) {
        isAlive = alive;
    }

    /**
     * 是否可繁殖
     *
     * @return
     */
    public abstract boolean isReproducible();

    public void die() {
        isAlive = false;
    }

    @Override
    public int getRow() {
        return location.getRow();
    }

    @Override
    public int getColumn() {
        return location.getColumn();
    }

    @Override
    public synchronized void setVersion(int version) {
        this.version.set(version);
    }

    public int getRemainTime() {
        return maxAliveTime - aliveTime;
    }

    /**
     * 死亡时间+1
     *
     * @return 死亡时间如果超过 指定时间+(maxAliveTime - aliveTime), 则返回false; 其他情况返回true
     */
    public boolean increaseDeathTime() {
        return (deathTime++ - (maxAliveTime - aliveTime)) <= (ConstantNum.ONE_YEAR_DAYS.value / 12);
    }

    public boolean isDie() {
        return !isAlive;
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + "{" +
                "isAlive=" + isAlive +
                ", location=" + location +
                ", version=" + version +
                ", maxLifetime=" + maxLifetime +
                ", maxAliveTime=" + maxAliveTime +
                ", adultTime=" + adultTime +
                ", aliveTime=" + aliveTime +
                '}';
    }
}
