package model.entity.biology.animal;

import model.entity.Location;
import model.entity.biology.Biology;
import model.entity.biology.plant.Plant;

import java.awt.*;
import java.util.ArrayList;

public class Sheep extends Animal {

    public Sheep() {
        this(0);
    }

    public Sheep(int aliveTime) {
        super(aliveTime,aliveTime + 15, 2 * 365 / 2,12 * 365 / 2);
    }

    @Override
    public boolean isReproducible() {
        return aliveTime >= adultTime && (aliveTime % 15 == 0);
    }

    @Override
    public void draw(Graphics g, int x, int y, int size) {
        g.setColor(new Color(0, 92, 255, (int) (getRemainTimePercent() * 255)));
        g.fillRect(x, y, size, size);

    }

    @Override
    public Biology breed() {
        Sheep ret = null;
        if (isReproducible()) {
            ret = new Sheep();
            ret.version=this.version;
        }
        return ret;
    }

    @Override
    public Biology eat(ArrayList<Biology> neighbour) {
        Biology ret = null;
        ArrayList<Plant> list = new ArrayList<>();
        for (Biology bio : neighbour) {
            if (bio instanceof Plant) {
                list.add((Plant) bio);
            }
        }
        if (!list.isEmpty()) {
            ret = list.get((int) (Math.random() * list.size()));
            maxAliveTime += 3;
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
