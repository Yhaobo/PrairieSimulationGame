package model.interfaces;

import model.entity.Location;

import java.awt.*;
import java.io.Serializable;

public interface Cell extends Serializable {
    long serialVersionUID = 42L;
    int REMAIN_TIME_WARNING = 5;
    /**
     * 这个游戏中一年只有一百天
     */
    int ONE_YEAR_DAYS = 100;

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
     * 返回纵坐标
     * @return
     */
    int getRow();

    /**
     * 返回横坐标
     * @return
     */
    int getColumn();

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

    /**
     * 设置版本号
     */
    void setVersion(int version);
}
