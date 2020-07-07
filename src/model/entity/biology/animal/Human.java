package model.entity.biology.animal;

import model.entity.Location;
import model.entity.biology.Biology;
import model.entity.biology.plant.Plant;

import java.awt.*;
import java.util.ArrayList;

public class Human extends Animal {
    private static final double PROBABILITY = 1;
    private boolean hero = false;

    public Human() {
        this(0);
    }

    public Human(int aliveTime,int maxLifetime) {
        super(aliveTime, aliveTime + 15, 16 * 365 / 2, maxLifetime);
    }

    public Human(int aliveTime) {
        this(aliveTime,80 * 365 / 2);
    }

    @Override
    public void draw(Graphics g, int x, int y, int size) {
        if (!hero) {
            g.setColor(new Color(255, 0, 0, (int) (getRemainTimePercent() * 255)));
        } else {
            g.setColor(Color.WHITE);
        }
        g.fillRect(x, y, size, size);
    }

    @Override
    public Biology breed() {
        Human ret = null;
        if (isReproducible() && Math.random() < PROBABILITY) {
            ret = new Human();
            ret.version=this.version;
        }
        return ret;
    }

    @Override
    public boolean isReproducible() {
        return aliveTime >= adultTime && (aliveTime % 365 == 0);
    }

    @Override
    public Biology eat(ArrayList<Biology> neighbour) {
        Biology ret = null;
        ArrayList<Biology> list = new ArrayList<>();
        for (Biology i : neighbour) {
            if (i instanceof Wolf || i instanceof Sheep || i instanceof Plant) {
                list.add(i);
            }
        }
        if (!list.isEmpty()) {
            //随机获取一只
            ret = list.get((int) (Math.random() * list.size()));
            if (ret instanceof Wolf) {
                maxAliveTime += 365;
                adultTime=0;
                hero = true;
            } else if (ret instanceof Sheep) {
                maxAliveTime += 30;
            } else {
                maxAliveTime += 2;
            }
        }
        return ret;
    }

    @Override
    public Location move(Location[] freeAdj) {
        Location ret = null;
        if (freeAdj.length > 0 && Math.random() < 0.5) {
            ret = freeAdj[(int) (Math.random() * freeAdj.length)];
        }
        return ret;
    }

}
