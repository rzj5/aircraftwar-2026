package edu.hitsz.aircraftwar.strategy;

import edu.hitsz.aircraftwar.aircraft.AbstractAircraft;
import edu.hitsz.aircraftwar.bullet.BaseBullet;
import edu.hitsz.aircraftwar.bullet.EnemyBullet;

import java.util.ArrayList;
import java.util.List;

public class DoubleStraightStrategy implements ShootStrategy {
    private final int power;
    private final int speedY;

    public DoubleStraightStrategy(int power, int speedY) {
        this.power = power;
        this.speedY = speedY;
    }

    @Override
    public List<BaseBullet> shoot(AbstractAircraft aircraft) {
        List<BaseBullet> bullets = new ArrayList<>();
        int centerX = aircraft.getLocationX();
        int bulletY = aircraft.getLocationY() + aircraft.getHeight() / 2;
        int offset = 18;
        bullets.add(new EnemyBullet(centerX - offset, bulletY, 0, speedY, power));
        bullets.add(new EnemyBullet(centerX + offset, bulletY, 0, speedY, power));
        return bullets;
    }
}
