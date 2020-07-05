package model.biology;

import model.Cell;
import model.Location;

import java.io.Serializable;
import java.util.ArrayList;

public abstract class Biology implements Serializable, Cell {
    private boolean isAlive = true;
    private int x;
    private int y;

    //    public Biology() {
//    }
    @Override
    public void setLocation(int row, int column) {
        this.y = row;
        this.x = column;
    }

    @Override
    public Location getLocation() {
        return new Location(y, x);
    }

    public abstract Biology feed(ArrayList<Biology> neighbour);

    public abstract Biology breed();


    public abstract void grow();

    public boolean isAlive() {
        return isAlive;
    }

    public void die() {
        isAlive = false;
    }

    public int getRow() {
        return y;
    }

    public int getColumn() {
        return x;
    }

//    public void setRow(int row) {
//        y=row;
//    }
//    public void setColumn(int Column) {
//        x=Column;
//    }
}
