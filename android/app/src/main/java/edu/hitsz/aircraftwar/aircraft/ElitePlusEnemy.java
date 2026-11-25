package edu.hitsz.aircraftwar.aircraft;

import edu.hitsz.aircraftwar.image.BitmapManager;
import edu.hitsz.aircraftwar.strategy.DoubleStraightStrategy;
import edu.hitsz.aircraftwar.util.GameConfig;

public class ElitePlusEnemy extends AbstractAircraft {

    public ElitePlusEnemy(int locationX, int locationY, int speedX, int speedY, int hp) {
        super(locationX, locationY, speedX, speedY, hp);
        this.image = BitmapManager.ELITE_PLUS_ENEMY;
        this.shootStrategy = new DoubleStraightStrategy(10, 12);
    }

    @Override
    public void forward() {
        super.forward();
        if (getLocationX() <= 0 || getLocationX() + getWidth() >= GameConfig.VIRTUAL_WIDTH) {
            setSpeedX(-getSpeedX());
        }
    }

    @Override
    public void onFreezeEffect() {
        if (isFrozen) {
            return;
        }
        isFrozen = true;
        originalSpeedX = this.speedX;
        originalSpeedY = this.speedY;
        this.speedX = 0;
        this.speedY = 0;

        new Thread(() -> {
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            this.speedX = originalSpeedX;
            this.speedY = originalSpeedY;
            isFrozen = false;
        }, "elite-plus-freeze").start();
    }
}
