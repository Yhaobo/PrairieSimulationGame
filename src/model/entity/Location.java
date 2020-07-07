package model.entity;

import java.io.Serializable;
import java.util.Objects;

public class Location implements Serializable {
    private int x;
    private int y;

    public Location(int row, int col) {
        this.y = row;
        this.x = col;
    }

    public int getColumn() {
        return x;
    }

    public int getRow() {
        return y;
    }

    public void setColumn(int Column) {
        this.x = Column;
    }

    public void setRow(int Row) {
        this.y = Row;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Location location = (Location) o;
        return x == location.x &&
                y == location.y;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }
}
