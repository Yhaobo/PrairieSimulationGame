package model.interfaces;

import model.Field;
import model.entity.Location;

/**
 * 玩家,被操控的对象
 *
 * @author Yhaobo
 * @date 2020/7/5
 */
public interface Player extends Cell{
    boolean isAlive();

    boolean isDie();

    Location front();

    Location back();

    Location left();

    Location right();

    void setFront(boolean b);

    void setBack(boolean b);

    void setLeft(boolean b);

    void setRight(boolean b);

    void playerMove(Location location);

    void playerEat();

    void playerQuickMove();

    public boolean isBreedFlag();

    public void setBreedFlag(boolean breedFlag);

    public void setField(Field field);

    void reset();
}
