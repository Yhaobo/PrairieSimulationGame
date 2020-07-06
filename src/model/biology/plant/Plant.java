package model.biology.plant;

import model.biology.Biology;

import java.awt.*;

public class Plant extends Biology {
    private int ageLimit=Integer.MAX_VALUE;
    private int adultAge=60;

    public Plant() {
        this(0);
    }

    public Plant(int aliveTime) {
        super(aliveTime,Integer.MAX_VALUE,60,Integer.MAX_VALUE);
    }

    @Override
    public Plant breed() {
        Plant ret = null;
        if (isReproducible()) {
            ret = new Plant();
        }
        return ret;
    }

    @Override
    public void grow() {
        aliveTime++;
        if (aliveTime >= ageLimit) {
            super.die();
        }
    }

    @Override
    public boolean isReproducible() {
        return aliveTime >= adultAge && (aliveTime % 60 == 0);
    }

    @Override
    public void draw(Graphics g, int x, int y, int size) {
        g.setColor(new Color(0, 255, 0, (int) (getRemainTimePercent() * 255)));
        g.fillRect(x, y, size, size);
    }
}
