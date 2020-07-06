package model;

import model.biology.animal.Human;

public class Actor extends Human implements Protagonist{
    public boolean front;
    public boolean left;
    public boolean right;
    public boolean back;

    public Actor() {
        super(-1);
    }

    @Override
    public boolean isReproducible() {
        return aliveTime >= adultTime && (aliveTime % 50 == 0);
    }

    @Override
    public Location move(Location[] freeAdj) {
        return null;
    }

    public int getRemainingTime() {
        return maxAliveTime - aliveTime;
    }

    public Location move() {
        Location location = new Location(getRow(), getColumn());
        if (front) {
            location.setRow(location.getRow() - 1);
//            return front();
        }
        if (left) {
//            return left();
            location.setColumn(location.getColumn() - 1);
        }
        if (right) {
//            return right();
            location.setColumn(location.getColumn() + 1);
        }
        if (back) {
//            return back();
            location.setRow(location.getRow() + 1);
        }
        return location;
    }

    public Location left() {
        return new Location(getRow(), getColumn() - 1);
    }

    public Location right() {
        return new Location(getRow(), getColumn() + 1);
    }

    public Location front() {
        return new Location(getRow() - 1, getColumn());
    }

    public Location back() {
        return new Location(getRow() + 1, getColumn());
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