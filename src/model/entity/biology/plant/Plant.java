package model.entity.biology.plant;

import model.entity.biology.Biology;
import util.ConstantNum;

import java.awt.*;

public class Plant extends Biology {
    public static final int BREED_SCOPE = 5;

    public Plant() {
        this(0);
    }

    public Plant(int aliveTime) {
        super(aliveTime, Integer.MAX_VALUE, ConstantNum.ONE_YEAR_DAYS.value/6, Integer.MAX_VALUE);
    }

    @Override
    public boolean isReproducible() {
        return aliveTime >= adultTime;
    }

    @Override
    public Biology breed() {
        Plant ret = null;
        if (isReproducible()) {
            ret = new Plant();
//            ret.setVersion(this.version.get());
        }
        return ret;
    }

    @Override
    public void draw(Graphics g, int x, int y, int size) {
//        if (isAlive()) {
        g.setColor(new Color(34, 139, 34));
//        } else {
//            g.setColor(new Color(107,142,35));
//        }
        g.fillRect(x, y, size, size);
    }
}
