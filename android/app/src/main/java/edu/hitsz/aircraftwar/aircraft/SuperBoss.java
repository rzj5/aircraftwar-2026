package edu.hitsz.aircraftwar.aircraft;

import edu.hitsz.aircraftwar.image.BitmapManager;
import edu.hitsz.aircraftwar.strategy.RingShootStrategy;
import edu.hitsz.aircraftwar.util.GameConfig;

public class SuperBoss extends AbstractAircraft {

    public SuperBoss(int locationX, int locationY, int speedX, int speedY, int hp) {
        super(locationX, locationY, speedX, speedY, hp);
        this.image = BitmapManager.SUPER_BOSS;
        this.shootStrategy = new RingShootStrategy(20, 8, false, 20);
    }

    @Override
    public void onFreezeEffect() {
        // Boss 不受冰冻影响
    }

    @Override
    public void forward() {
        setLocationX(getLocationX() + getSpeedX());
        if (getLocationX() <= 0 || getLocationX() + getWidth() >= GameConfig.VIRTUAL_WIDTH) {
            setSpeedX(-getSpeedX());
        }
    }
}
