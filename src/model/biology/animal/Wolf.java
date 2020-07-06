package model.biology.animal;

import model.Location;
import model.biology.Biology;

import java.awt.*;
import java.util.ArrayList;

public class Wolf extends Animal {
    public boolean huntHumanFlag;

    public Wolf() {
        this(0);
    }

    public Wolf(int aliveTime) {
        super(aliveTime,aliveTime+50, 3 * 365/2,20 * 365/2);
    }

    @Override
    public void draw(Graphics g, int x, int y, int size) {
        g.setColor(new Color(0, 0, 0, (int) (getRemainTimePercent() * 255)));
        g.fillRect(x, y, size, size);

    }


    @Override
    public Animal breed() {
        Animal ret = null;
        if (isReproducible()) {
            ret = new Wolf();
        }
        return ret;
    }

    @Override
    public boolean isReproducible() {
        return aliveTime >= adultTime && (aliveTime % 30 == 0);
    }

    @Override
    public Biology eat(ArrayList<Biology> neighbour) {
        Biology ret = null;
        ArrayList<Biology> sheeps = new ArrayList<>();
        ArrayList<Human> humans = new ArrayList<>();
        for (Biology bio : neighbour) {
            if (bio instanceof Sheep) {
                sheeps.add(bio);
            }
            if (bio instanceof Human) {
                humans.add((Human) bio);
            }
        }
        if (!sheeps.isEmpty()) {
            ret = sheeps.get((int) (Math.random() * sheeps.size()));
            maxAliveTime += 50;
        } else if (!humans.isEmpty()) {
            ret = humans.get((int) (Math.random() * humans.size()));
            maxAliveTime += 500;
        }
        return ret;
    }

    @Override
    public Location move(Location[] freeAdj) {
        Location ret = null;
        if (freeAdj.length > 0) {
            ret = freeAdj[(int) (Math.random() * freeAdj.length)];
        }
        return ret;
    }

}
