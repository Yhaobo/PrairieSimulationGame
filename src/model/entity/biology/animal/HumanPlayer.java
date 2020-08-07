package model.entity.biology.animal;

import model.Field;
import model.GameAudio;
import model.entity.Location;
import model.entity.biology.Biology;
import model.interfaces.Player;
import util.ConstantNum;

import java.util.List;

public class HumanPlayer extends Human implements Player {
    private boolean front;
    private boolean left;
    private boolean right;
    private boolean back;
    /**
     * 由玩家控制是否繁殖
     */
    private boolean breedFlag;
    private Field field;

    public HumanPlayer(Field field) {
        super(0, 0);
        this.field = field;
    }

    public HumanPlayer() {

    }

    @Override
    public void playerMove(Location location) {
        field.move(this, location);
    }

    @Override
    public void playerEat() {
        List<Biology> neighbour = field.getNeighbour(getRow(), getColumn(), 1);
        Biology prey = eat(neighbour);
        if (prey != null) {
            field.replace(this, prey);
            if (prey instanceof Sheep) {
                GameAudio.getSheepAudio().play();
            } else if (prey instanceof Wolf) {
                GameAudio.getWolfAudio();
            }
        }
    }

    @Override
    public boolean grow() {
        if (getRemainTime() == ConstantNum.REMAIN_TIME_WARNING.value - 1) {
            GameAudio.getHungry().play();
        }
        if (aliveTime++ > maxAliveTime) {
            System.out.println("die");
            die();
            return false;
        }
        return true;
    }

    @Override
    public Biology breed() {
        Human baby = null;
        if (isReproducible()) {
            baby = new Human();
            baby.setVersion(version.get());
        }

        return baby;
    }

    @Override
    public void setField(Field field) {
        this.field = field;
    }

    @Override
    public void reset() {
        setVersion(0);
        aliveTime = 0;
        maxAliveTime = 5;
        setAlive(true);
    }

    @Override
    public boolean isBreedFlag() {
        return breedFlag;
    }

    @Override
    public void setBreedFlag(boolean breedFlag) {
        this.breedFlag = breedFlag;
    }

    @Override
    public boolean isReproducible() {
        return aliveTime >= adultTime;
    }

    @Override
    public boolean isAlive() {
        return field.getCell(getRow(), getColumn()) == this && super.isAlive();
    }

    @Override
    public boolean isDie() {
        return field.getCell(getRow(), getColumn()) != this || super.isDie();
    }

    @Override
    public void playerQuickMove() {
        Location location = new Location(getRow(), getColumn());
        if (front) {
            location.setRow(location.getRow() - 1);
        }
        if (left) {
            location.setColumn(location.getColumn() - 1);
        }
        if (right) {
            location.setColumn(location.getColumn() + 1);
        }
        if (back) {
            location.setRow(location.getRow() + 1);
        }
        field.move(this, location);
    }

    @Override
    public Location left() {
        return new Location(getRow(), getColumn() - 1);
    }

    @Override
    public Location right() {
        return new Location(getRow(), getColumn() + 1);
    }

    @Override
    public Location front() {
        return new Location(getRow() - 1, getColumn());
    }

    @Override
    public Location back() {
        return new Location(getRow() + 1, getColumn());
    }

    public boolean isFront() {
        return front;
    }

    @Override
    public void setFront(boolean front) {
        this.front = front;
    }

    public boolean isLeft() {
        return left;
    }

    @Override
    public void setLeft(boolean left) {
        this.left = left;
    }

    public boolean isRight() {
        return right;
    }

    @Override
    public void setRight(boolean right) {
        this.right = right;
    }

    public boolean isBack() {
        return back;
    }

    @Override
    public void setBack(boolean back) {
        this.back = back;
    }
//    @Override
//    public void draw(Graphics g, int x, int y, int size) {
//        int alpha = (int) ((1 - getAgePercent()) * 255);
////        int alpha =255;
//        g.setColor(new Color(254, 0, 255, alpha));
////        g.fillRect(x, y, size, size);
////        g.drawRect(x,y,size,size);
////        g.fillOval(x,y,size,size);
//        g.fill3DRect(x,y,size,size,true);
////        g.fillRoundRect(x,y,size,size,7,7);
//    }
}