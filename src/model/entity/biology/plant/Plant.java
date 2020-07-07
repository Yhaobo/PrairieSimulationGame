package model.entity.biology.plant;

import model.entity.biology.Biology;

import java.awt.*;

public class Plant extends Biology {

    public Plant() {
        this(0);
    }

    public Plant(int aliveTime) {
        super(aliveTime,Integer.MAX_VALUE,60,Integer.MAX_VALUE);
    }

    @Override
    public Biology breed() {
        Plant ret = null;
        if (isReproducible()) {
            ret = new Plant();
            ret.version=this.version;
        }
        return ret;
    }


    @Override
    public boolean isReproducible() {
        return aliveTime >= adultTime && (aliveTime % 60 == 0);
    }

    @Override
    public void draw(Graphics g, int x, int y, int size) {
        g.setColor(new Color(0, 255, 0, (int) (getRemainTimePercent() * 255)));
        g.fillRect(x, y, size, size);
    }
}
