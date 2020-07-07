package model.entity.biology.animal;

import model.entity.Location;
import model.entity.biology.Biology;

import java.util.ArrayList;

public abstract class Animal extends Biology {

    public Animal(int aliveTime,int maxAliveTime, int adultAge, int maxLifetime) {
        super(aliveTime, maxAliveTime, adultAge, maxLifetime);
    }

    public void setAdultAge(int adultTime) {
        super.adultTime = adultTime;
    }


    public abstract Biology eat(ArrayList<Biology> neighbour);

    public Location move(Location[] freeAdj) {
        Location ret = null;
        if (freeAdj.length > 0 && Math.random() < 0.25) {
            ret = freeAdj[(int) (Math.random() * freeAdj.length)];
        }
        return ret;
    }
}
