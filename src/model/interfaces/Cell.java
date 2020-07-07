package model.interfaces;

import model.entity.Location;

import java.awt.*;

public interface Cell {
    int REMAIN_TIME_WARNING = 10;

    /**
     * 2D绘画
     *
     * @param g
     * @param x
     * @param y
     * @param size
     */
    void draw(Graphics g, int x, int y, int size);

    /**
     * 设置位置
     *
     * @param row    几行
     * @param column 几列
     */
    void setLocation(int row, int column);

    /**
     * 返回位置
     *
     * @return
     */
    Location getLocation();

    /**
     * 每回合版本号+1, 此方法是为了防止一个Cell在一回合内多次行动
     *
     * @param newVersion 最新的版本号
     * @return 如果newVersion-1等于当前版本号,返回true;否则返回false
     */
    boolean compareVersion(int newVersion);

    /**
     * 剩余存活时间小于等于REMAIN_TIME_WARNING时,透明度会变化
     *
     * @return 返回剩余存活时间和REMAIN_TIME_WARNING的比值
     */
    double getRemainTimePercent();
}
