package model.entity;

import java.io.Serializable;

public class Location implements Serializable {
    private int row;
    private int col;

    public Location(int row, int col) {
        this.row = row;
        this.col = col;
    }

    public int getColumn() {
        return col;
    }

    public int getRow() {
        return row;
    }

    public void setColumn(int Column) {
        this.col = Column;
    }

    public void setRow(int Row) {
        this.row = Row;
    }

    @Override
    public String toString() {
        return "Location{" +
                "row=" + row +
                ", col=" + col +
                '}';
    }
}
