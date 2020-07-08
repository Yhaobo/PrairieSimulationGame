package model.entity.biology.plant;

import model.entity.biology.Biology;

import java.awt.*;

public class Plant extends Biology {

    public Plant() {
        this(0);
    }

    public Plant(int aliveTime) {
        super(aliveTime, Integer.MAX_VALUE, ONE_YEAR_DAYS, Integer.MAX_VALUE);
    }

    @Override
    public boolean isReproducible() {
        return aliveTime >= adultTime && (aliveTime % ONE_YEAR_DAYS / 4 == 0);
    }

    @Override
    public Biology breed() {
        Plant ret = null;
        if (isReproducible()) {
            ret = new Plant();
            ret.version = this.version;
        }
        return ret;
    }

    @Override
    public void draw(Graphics g, int x, int y, int size) {
        g.setColor(new Color(34, 139, 34, (int) (getRemainTimePercent() * 255)));
        g.fillRect(x, y, size, size);
//        g.fill3DRect(x, y, size, size, true);
    }
}
