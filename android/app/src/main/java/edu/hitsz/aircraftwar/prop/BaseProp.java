package edu.hitsz.aircraftwar.prop;

import edu.hitsz.aircraftwar.aircraft.HeroAircraft;
import edu.hitsz.aircraftwar.basic.AbstractFlyingObject;

public abstract class BaseProp extends AbstractFlyingObject {
    protected int activeTime;

    public BaseProp(int locationX, int locationY, int speedX, int speedY) {
        super(locationX, locationY, speedX, speedY);
    }

    public abstract void active(HeroAircraft heroAircraft);
}
