package edu.hitsz.aircraftwar.aircraft;

import android.graphics.Bitmap;

import edu.hitsz.aircraftwar.basic.AbstractFlyingObject;
import edu.hitsz.aircraftwar.bullet.BaseBullet;
import edu.hitsz.aircraftwar.strategy.ShootStrategy;
import edu.hitsz.aircraftwar.strategy.SingleStraightStrategy;
import edu.hitsz.aircraftwar.util.GameConfig;

import java.util.List;

public abstract class AbstractAircraft extends AbstractFlyingObject implements GameObserver {
    protected boolean dropProp = true;
    protected int originalSpeedX;
    protected int originalSpeedY;
    protected boolean isFrozen = false;
    protected int maxHp;
    protected int hp;
    protected int shootCounter = 0;
    protected static final int SHOOT_INTERVAL = 40;
    protected ShootStrategy shootStrategy;

    public AbstractAircraft(int locationX, int locationY, int speedX, int speedY, int hp) {
        super(locationX, locationY, speedX, speedY);
        this.hp = hp;
        this.maxHp = hp;
    }

    public boolean isDropProp() {
        return dropProp;
    }

    public void setDropProp(boolean dropProp) {
        this.dropProp = dropProp;
    }

    @Override
    public void onBombEffect() {
    }

    @Override
    public void onFreezeEffect() {
    }

    @Override
    public Bitmap getImage() {
        return this.image;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }

    public boolean canShoot() {
        shootCounter++;
        if (shootCounter >= SHOOT_INTERVAL) {
            shootCounter = 0;
            return true;
        }
        return false;
    }

    public void decreaseHp(int decrease) {
        hp -= decrease;
        if (hp <= 0) {
            hp = 0;
            vanish();
        }
    }

    public int getHp() {
        return hp;
    }

    public int getMaxHp() {
        return maxHp;
    }

    public void setHp(int hp) {
        this.hp = Math.max(0, Math.min(hp, maxHp));
    }

    public List<BaseBullet> shoot() {
        return shootStrategy.shoot(this);
    }

    public void setShootStrategy(ShootStrategy shootStrategy) {
        this.shootStrategy = shootStrategy;
    }

    public void reset() {
        this.hp = this.maxHp;
        this.locationX = GameConfig.VIRTUAL_WIDTH / 2;
        this.locationY = GameConfig.VIRTUAL_HEIGHT - 100;
        this.setValid(true);
        this.shootStrategy = new SingleStraightStrategy(30, -12, true);
    }
}
