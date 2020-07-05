package model.biology.plant;

import model.biology.Biology;

import java.awt.*;
import java.util.ArrayList;

public class Grass extends Plant {
    public Grass(){
        super(Integer.MAX_VALUE, 60);
    }
    public Grass(int age) {
        super(Integer.MAX_VALUE, 60);
        this.age = age;
    }

    @Override
    public void draw(Graphics g, int x, int y, int size) {
//        int alpha = (int) ((1 - getAgePercent()) * 255);
//        int alpha = 255;
        g.setColor(new Color(0, 255, 0, 255));
        g.fillRect(x, y, size, size);
    }

    @Override
    public Plant breed() {
        Plant ret = null;
        if (isBreedable() ) {
            ret = new Grass();
        }
        return ret;
    }

    public String toString() {
        return "Grass:" + super.toString();
    }

    @Override
    public Biology feed(ArrayList<Biology> neighbour) {
        return null;
    }

    @Override
    public void grow() {
            age++;
//            if (age >= ageLimit) {
//                die();
//        }
    }
}