package edu.hitsz.strategy;

import edu.hitsz.aircraft.AbstractAircraft;
import edu.hitsz.bullet.BaseBullet;
import edu.hitsz.bullet.EnemyBullet;
import edu.hitsz.bullet.HeroBullet;
import java.util.ArrayList;
import java.util.List;

/**
 * 单排直射策略
 * 对应精英敌机、无道具的英雄机
 */
public class SingleStraightStrategy implements ShootStrategy {
    // 子弹伤害
    private final int power;
    // 子弹Y方向速度
    private final int speedY;
    // 是否是英雄机的子弹（区分子弹类型和飞行方向）
    private final boolean isHero;

    /**
     * 构造方法
     * @param power 子弹伤害
     * @param speedY 子弹Y方向速度（正数向下，负数向上）
     * @param isHero 是否是英雄机的子弹
     */
    public SingleStraightStrategy(int power, int speedY, boolean isHero) {
        this.power = power;
        this.speedY = speedY;
        this.isHero = isHero;
    }

    @Override
    public List<BaseBullet> shoot(AbstractAircraft aircraft) {
        List<BaseBullet> bullets = new ArrayList<>();

        // ========== 完全适配你的中心点坐标逻辑 ==========
        // 1. 子弹X坐标：直接用飞机的中心点locationX，完美居中，不会再偏右
        int bulletX = aircraft.getLocationX();

        // 2. 子弹Y坐标：适配中心点坐标，区分英雄机和敌机
        int bulletY;
        int aircraftHalfHeight = aircraft.getHeight() / 2;
        if (isHero) {
            // 英雄机：子弹从飞机顶部（中心点向上半高）发射，向上飞
            bulletY = aircraft.getLocationY() - aircraftHalfHeight;
        } else {
            // 精英敌机：子弹从飞机底部（中心点向下半高）发射，向下飞
            bulletY = aircraft.getLocationY() + aircraftHalfHeight;
        }
        // ==============================================

        // 子弹生成逻辑完全保留
        if (isHero) {
            bullets.add(new HeroBullet(bulletX, bulletY, 0, speedY, power));
        } else {
            bullets.add(new EnemyBullet(bulletX, bulletY, 0, speedY, power));
        }
        return bullets;
    }


}
