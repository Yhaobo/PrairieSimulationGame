package model.biology.animal;

import model.Location;
import model.biology.Biology;

import java.awt.*;
import java.util.ArrayList;

public class Wolf extends Animal {
    public static final int LIFE_TIME = 20 * 365/2;
    public boolean huntHumanFlag;

    public Wolf() {
        super(50, 3 * 365/2);
    }

    public Wolf(int age) {
        super(age + 50, 3 * 365/2);
        this.age = age;
    }

    @Override
    public void draw(Graphics g, int x, int y, int size) {
        int alpha = (int) ((1 - getAgePercent()) * 255);
//        int alpha =255;
        g.setColor(new Color(0, 0, 0, alpha));
        g.fillRect(x, y, size, size);

    }


    @Override
    public Animal breed() {
        Animal ret = null;
        if (isBreedable()) {
            ret = new Wolf();
        }
        return ret;
    }

    @Override
    public boolean isBreedable() {
        return age >= breedableAge && (age % 30 == 0);
    }

    @Override
    public Biology feed(ArrayList<Biology> neighbour) {
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
            longerLife(50,LIFE_TIME);
        } else if (!humans.isEmpty()) {
            ret = humans.get((int) (Math.random() * humans.size()));
            longerLife(500,LIFE_TIME);
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

    @Override
    public void grow() {
        age++;
        if (age >= ageLimit) {
            die();
        } else if (age >= LIFE_TIME) {
            die();
        }
    }
}
