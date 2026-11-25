package edu.hitsz.aircraftwar.strategy;

import edu.hitsz.aircraftwar.aircraft.AbstractAircraft;
import edu.hitsz.aircraftwar.bullet.BaseBullet;
import edu.hitsz.aircraftwar.bullet.EnemyBullet;
import edu.hitsz.aircraftwar.bullet.HeroBullet;

import java.util.ArrayList;
import java.util.List;

public class RingShootStrategy implements ShootStrategy {
    private final int power;
    private final int bulletSpeed;
    private final boolean isHero;
    private final int bulletCount;

    public RingShootStrategy(int power, int bulletSpeed, boolean isHero, int bulletCount) {
        this.power = power;
        this.bulletSpeed = bulletSpeed;
        this.isHero = isHero;
        this.bulletCount = bulletCount;
    }

    @Override
    public List<BaseBullet> shoot(AbstractAircraft aircraft) {
        List<BaseBullet> bullets = new ArrayList<>();
        int centerX = aircraft.getLocationX();
        int centerY = aircraft.getLocationY();
        double angleStep = 2 * Math.PI / bulletCount;
        for (int i = 0; i < bulletCount; i++) {
            double angle = i * angleStep;
            int speedX = (int) (bulletSpeed * Math.cos(angle));
            int speedY = (int) (bulletSpeed * Math.sin(angle));
            if (isHero) {
                bullets.add(new HeroBullet(centerX, centerY, speedX, speedY, power));
            } else {
                bullets.add(new EnemyBullet(centerX, centerY, speedX, speedY, power));
            }
        }
        return bullets;
    }
}
