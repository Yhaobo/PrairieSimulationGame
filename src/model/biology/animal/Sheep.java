package model.biology.animal;

import model.Location;
import model.biology.Biology;
import model.biology.plant.Grass;

import java.awt.*;
import java.util.ArrayList;

public class Sheep extends Animal {
    public static final int LIFE_TIME = 12 * 365/2;

    public Sheep() {
        super(15, 2 * 365/2);
    }

    public Sheep(int age) {
        super(age + 15, 2 * 365/2);
        this.age = age;
    }

    @Override
    public void grow() {
        age++;
        if (age >= ageLimit) {
            die();
        } else if (age >= LIFE_TIME) {
            die();
        }
    }

    @Override
    public boolean isBreedable() {
        return age >= breedableAge && (age % 15 == 0);
    }

    @Override
    public void draw(Graphics g, int x, int y, int size) {
        int alpha = (int) ((1 - getAgePercent()) * 255);
//        int alpha =255;
        g.setColor(new Color(0, 92, 255, alpha));// (int)((20-getAge())/20.0*255)));
        g.fillRect(x, y, size, size);

    }

    @Override
    public Animal breed() {
        Animal ret = null;
        if (isBreedable()) {
            ret = new Sheep();
        }
        return ret;
    }

    @Override
    public Biology feed(ArrayList<Biology> neighbour) {
        Biology ret = null;
        ArrayList<Grass> list = new ArrayList<>();
        for (Biology bio : neighbour) {
            if (bio instanceof Grass) {
                list.add((Grass) bio);
            }
        }
        if (!list.isEmpty()) {
            ret = list.get((int) (Math.random() * list.size()));
            longerLife(3, LIFE_TIME);
        }
        return ret;
    }

    public Location move(Location[] freeAdj) {
        Location ret = null;
        if (freeAdj.length > 0) {
            ret = freeAdj[(int) (Math.random() * freeAdj.length)];
        }
        return ret;
    }
}
