package edu.hitsz.aircraftwar.aircraft;

import edu.hitsz.aircraftwar.image.BitmapManager;
import edu.hitsz.aircraftwar.strategy.ScatterStrategy;
import edu.hitsz.aircraftwar.util.GameConfig;

public class BossEnemy extends AbstractAircraft {

    public BossEnemy(int locationX, int locationY, int speedX, int speedY, int hp) {
        super(locationX, locationY, speedX, speedY, hp);
        this.image = BitmapManager.BOSS_ENEMY;
        this.shootStrategy = new ScatterStrategy(15, 10, false, 3);
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
        }, "boss-freeze").start();
    }

    @Override
    public void forward() {
        super.forward();
        if (getLocationX() <= 0 || getLocationX() + getWidth() >= GameConfig.VIRTUAL_WIDTH) {
            setSpeedX(-getSpeedX());
        }
    }
}
