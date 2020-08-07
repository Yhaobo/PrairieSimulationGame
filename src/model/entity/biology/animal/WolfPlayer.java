package model.entity.biology.animal;

import model.Field;
import model.entity.Location;
import model.interfaces.Player;

/**
 * @author Yhaobo
 * @date 2020/7/10
 */
public class WolfPlayer extends Wolf implements Player {

    @Override
    public Location front() {
        return null;
    }

    @Override
    public Location back() {
        return null;
    }

    @Override
    public Location left() {
        return null;
    }

    @Override
    public Location right() {
        return null;
    }

    @Override
    public void setFront(boolean b) {

    }

    @Override
    public void setBack(boolean b) {

    }

    @Override
    public void setLeft(boolean b) {

    }

    @Override
    public void setRight(boolean b) {

    }

    @Override
    public void playerMove(Location location) {

    }

    @Override
    public void playerEat() {

    }

    @Override
    public void playerQuickMove() {

    }

    @Override
    public boolean isBreedFlag() {
        return false;
    }

    @Override
    public void setBreedFlag(boolean breedFlag) {

    }

    @Override
    public void setField(Field field) {

    }

    @Override
    public void reset() {

    }
}
