package model.biology.animal;

import model.Location;
import model.biology.Biology;
import model.biology.plant.Grass;

import java.awt.*;
import java.util.ArrayList;

public class Human extends Animal {
    public static  long LIFE_TIME = 80 * 365/2;
    private static final double probability = 1;
    private int alpha;
    private boolean hero = false;

    public Human() {
        super(15, 16 * 365/2);
    }

    public Human(int age) {
        super(age + 15, 16 * 365/2);
        this.age = age;
    }

    @Override
    public void draw(Graphics g, int x, int y, int size) {

        alpha = (int) ((1 - getAgePercent()) * 255);
//        int alpha = 255;
        if (!hero) {
            g.setColor(new Color(255, 0, 0, alpha));
        } else {
            g.setColor(Color.WHITE);
        }
        g.fillRect(x, y, size, size);
    }

    @Override
    public Animal breed() {
        Animal ret = null;
        if (isBreedable() && Math.random() < probability) {
            ret = new Human();
        }
        return ret;
    }

    @Override
    public Biology feed(ArrayList<Biology> neighbour) {
        Biology ret = null;
        ArrayList<Biology> list = new ArrayList<>();
        for (Biology i : neighbour) {
//			if(i instanceof Human) {
//			}else {
//				list.add(i);
//			}
            if (i instanceof Wolf || i instanceof Sheep || i instanceof Grass) {
                list.add(i);
            }
//			if(i instanceof Sheep) {
//				list.add(i);
//			}
//			if(i instanceof Grass) {
//				list.add(i);
//			}
//			if(i instanceof Fox) {
//				list.add(i);
//			}
        }
        if (!list.isEmpty()) {
            ret = list.get((int) (Math.random() * list.size()));//随机获取一只
            if (ret instanceof Wolf) {
                longerLife(365, LIFE_TIME);
                super.setBreedableAge(0);
                hero = true;
            } else if (ret instanceof Sheep) {
                longerLife(30, LIFE_TIME);
            } else {
                longerLife(2, LIFE_TIME);
            }
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

    public Location move(Location[] freeAdj) {
        Location ret = null;
        if (freeAdj.length > 0 && Math.random() < 0.5) {
            ret = freeAdj[(int) (Math.random() * freeAdj.length)];
        }
        return ret;
    }

}
