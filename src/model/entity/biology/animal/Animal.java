package model.entity.biology.animal;

import model.entity.Location;
import model.entity.biology.Biology;

import java.util.ArrayList;

public abstract class Animal extends Biology {

    public Animal(int aliveTime, int maxAliveTime, int adultAge, int maxLifetime) {
        super(aliveTime, maxAliveTime, adultAge, maxLifetime);
    }

    public abstract Biology eat(ArrayList<Biology> neighbour);

    public abstract Location move(Location[] freeAdj);

}
