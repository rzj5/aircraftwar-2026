package edu.hitsz.aircraftwar.bullet;

import edu.hitsz.aircraftwar.aircraft.GameObserver;
import edu.hitsz.aircraftwar.basic.AbstractFlyingObject;
import edu.hitsz.aircraftwar.util.GameConfig;

public abstract class BaseBullet extends AbstractFlyingObject implements GameObserver {
    private int power = 0;
    protected int originalSpeedX;
    protected int originalSpeedY;
    protected boolean isFrozen = false;

    public BaseBullet(int locationX, int locationY, int speedX, int speedY, int power) {
        super(locationX, locationY, speedX, speedY);
        this.power = power;
    }

    @Override
    public void forward() {
        locationX += speedX;
        locationY += speedY;

        if (locationX <= 0 || locationX >= GameConfig.VIRTUAL_WIDTH) {
            vanish();
        }
        if (speedY > 0 && locationY >= GameConfig.VIRTUAL_HEIGHT) {
            vanish();
        } else if (locationY <= 0) {
            vanish();
        }
    }

    public int getPower() {
        return power;
    }

    @Override
    public void onBombEffect() {
        vanish();
    }

    @Override
    public void onFreezeEffect() {
    }
}
