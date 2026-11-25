package edu.hitsz.aircraftwar.aircraft;

import edu.hitsz.aircraftwar.bullet.BaseBullet;
import edu.hitsz.aircraftwar.image.BitmapManager;
import edu.hitsz.aircraftwar.util.GameConfig;

import java.util.LinkedList;
import java.util.List;

/**
 * 普通敌机：不可射击、不掉落道具
 */
public class MobEnemy extends AbstractAircraft {

    public MobEnemy(int locationX, int locationY, int speedX, int speedY, int hp) {
        super(locationX, locationY, speedX, speedY, hp);
        this.image = BitmapManager.MOB_ENEMY;
    }

    @Override
    public void forward() {
        super.forward();
        if (locationY >= GameConfig.VIRTUAL_HEIGHT) {
            vanish();
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
    }

    @Override
    public List<BaseBullet> shoot() {
        return new LinkedList<>();
    }
}
