package model.biology.plant;

import model.biology.Biology;

public abstract class Plant extends Biology {
    private int ageLimit;
    private int breedableAge;
    protected int age;

    public Plant(int ageLimit, int breedableAge) {
        this.ageLimit = ageLimit;
        this.breedableAge = breedableAge;
    }

//    protected double getAgePercent() {
//        return (double) age / ageLimit;
//    }

    public abstract Plant breed();

    public void grow() {
        age++;
        if (age >= ageLimit) {
            super.die();
        }
    }

    public boolean isBreedable() {
        return age >= breedableAge && (age % 60 == 0);
    }

}
