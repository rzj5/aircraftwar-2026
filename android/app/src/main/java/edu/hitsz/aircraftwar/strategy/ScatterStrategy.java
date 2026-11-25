package edu.hitsz.aircraftwar.strategy;

import edu.hitsz.aircraftwar.aircraft.AbstractAircraft;
import edu.hitsz.aircraftwar.bullet.BaseBullet;
import edu.hitsz.aircraftwar.bullet.EnemyBullet;
import edu.hitsz.aircraftwar.bullet.HeroBullet;

import java.util.ArrayList;
import java.util.List;

public class ScatterStrategy implements ShootStrategy {
    private final int power;
    private final int speedY;
    private final boolean isHero;
    private final int bulletCount;

    public ScatterStrategy(int power, int speedY, boolean isHero, int bulletCount) {
        this.power = power;
        this.speedY = speedY;
        this.isHero = isHero;
        this.bulletCount = bulletCount;
    }

    @Override
    public List<BaseBullet> shoot(AbstractAircraft aircraft) {
        List<BaseBullet> bullets = new ArrayList<>();
        int baseX = aircraft.getLocationX();
        int halfHeight = aircraft.getHeight() / 2;
        int baseY = isHero ? aircraft.getLocationY() - halfHeight : aircraft.getLocationY() + halfHeight;
        int speedXStep = 4;
        for (int i = 0; i < bulletCount; i++) {
            int speedX = (i - (bulletCount - 1) / 2) * speedXStep;
            if (isHero) {
                bullets.add(new HeroBullet(baseX, baseY, speedX, speedY, power));
            } else {
                bullets.add(new EnemyBullet(baseX, baseY, speedX, speedY, power));
            }
        }
        return bullets;
    }
}
