package edu.hitsz.aircraftwar.strategy;

import edu.hitsz.aircraftwar.aircraft.AbstractAircraft;
import edu.hitsz.aircraftwar.bullet.BaseBullet;
import edu.hitsz.aircraftwar.bullet.EnemyBullet;
import edu.hitsz.aircraftwar.bullet.HeroBullet;

import java.util.ArrayList;
import java.util.List;

public class SingleStraightStrategy implements ShootStrategy {
    private final int power;
    private final int speedY;
    private final boolean isHero;

    public SingleStraightStrategy(int power, int speedY, boolean isHero) {
        this.power = power;
        this.speedY = speedY;
        this.isHero = isHero;
    }

    @Override
    public List<BaseBullet> shoot(AbstractAircraft aircraft) {
        List<BaseBullet> bullets = new ArrayList<>();
        int bulletX = aircraft.getLocationX();
        int halfHeight = aircraft.getHeight() / 2;
        int bulletY = isHero ? aircraft.getLocationY() - halfHeight : aircraft.getLocationY() + halfHeight;
        if (isHero) {
            bullets.add(new HeroBullet(bulletX, bulletY, 0, speedY, power));
        } else {
            bullets.add(new EnemyBullet(bulletX, bulletY, 0, speedY, power));
        }
        return bullets;
    }
}
