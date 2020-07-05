package model.biology.animal;

import model.Location;
import model.biology.Biology;

public abstract class Animal extends Biology {
    protected int ageLimit;
    protected int breedableAge;
    protected int age = 0;

    public Animal(int ageLimit, int breedableAge) {
        this.ageLimit = ageLimit;
        this.breedableAge = breedableAge;
    }

    public void setBreedableAge(int breedableAge) {
        this.breedableAge = breedableAge;
    }

    protected double getAgePercent() {
        byte num=5;//剩余存活天数小于等于num时,颜色会变化
        if (ageLimit - age <= num) {
            return (double) (num-(ageLimit-age))/num;
        }
       return 0;
    }

    public void grow() {
        age++;
        if (age >= ageLimit) {
            super.die();
        }
    }


    public boolean isBreedable() {
        return age >= breedableAge&&(age%365==0);
    }

    public Location move(Location[] freeAdj) {
        Location ret = null;
        if (freeAdj.length > 0 && Math.random() < 0.25) {
            ret = freeAdj[(int) (Math.random() * freeAdj.length)];
        }
        return ret;
    }

    public void longerLife(int age,long lifeTime) {
        if (ageLimit <= (lifeTime - age)) {
            ageLimit+=age;
        }
    }
}
