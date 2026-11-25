package edu.hitsz.strategy;

import edu.hitsz.aircraft.AbstractAircraft;
import edu.hitsz.bullet.BaseBullet;
import edu.hitsz.bullet.EnemyBullet;
import java.util.ArrayList;
import java.util.List;

/**
 * 双排直射策略
 * 对应精锐敌机
 */
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
        int centerX = aircraft.getLocationX() + aircraft.getWidth() / 2;
        int bulletY = aircraft.getLocationY() + aircraft.getHeight();
        // 双发子弹左右偏移量
        int offset = 18;

        // 生成2颗左右分开的子弹
        bullets.add(new EnemyBullet(centerX - offset, bulletY, 0, speedY, power));
        bullets.add(new EnemyBullet(centerX + offset, bulletY, 0, speedY, power));
        return bullets;
    }
}
