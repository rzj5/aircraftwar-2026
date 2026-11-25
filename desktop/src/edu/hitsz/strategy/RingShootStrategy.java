package edu.hitsz.strategy;

import edu.hitsz.aircraft.AbstractAircraft;
import edu.hitsz.bullet.BaseBullet;
import edu.hitsz.bullet.EnemyBullet;
import edu.hitsz.bullet.HeroBullet;
import java.util.ArrayList;
import java.util.List;

/**
 * 环射策略
 * 对应Boss敌机、超级火力道具生效后的英雄机
 */
public class RingShootStrategy implements ShootStrategy {
    // 子弹伤害
    private final int power;
    // 子弹飞行速度
    private final int bulletSpeed;
    // 是否是英雄机的子弹
    private final boolean isHero;
    // 环射的子弹总数
    private final int bulletCount;

    /**
     * 构造方法
     * @param power 子弹伤害
     * @param bulletSpeed 子弹飞行速度（环射用统一的速度值）
     * @param isHero 是否是英雄机的子弹
     * @param bulletCount 环射的子弹总数
     */
    public RingShootStrategy(int power, int bulletSpeed, boolean isHero, int bulletCount) {
        this.power = power;
        this.bulletSpeed = bulletSpeed;
        this.isHero = isHero;
        this.bulletCount = bulletCount;
    }

    @Override
    public List<BaseBullet> shoot(AbstractAircraft aircraft) {
        List<BaseBullet> bullets = new ArrayList<>();

        // ========== 完全适配你的中心点坐标逻辑 ==========
        // 环射的子弹从飞机中心发射，直接用飞机的中心点坐标
        int centerX = aircraft.getLocationX();
        int centerY = aircraft.getLocationY();
        // ==============================================

        // 360度环射，按子弹数量均分角度
        double angleStep = 2 * Math.PI / bulletCount;
        for (int i = 0; i < bulletCount; i++) {
            double angle = i * angleStep;
            // 计算子弹的X/Y方向速度，实现360度飞行
            int speedX = (int) (bulletSpeed * Math.cos(angle));
            int speedY = (int) (bulletSpeed * Math.sin(angle));

            // 生成子弹
            if (isHero) {
                bullets.add(new HeroBullet(centerX, centerY, speedX, speedY, power));
            } else {
                bullets.add(new EnemyBullet(centerX, centerY, speedX, speedY, power));
            }
        }

        // 调试日志，运行时看控制台有没有输出
        System.out.println("===== 执行环射策略 =====");
        System.out.println("生成子弹数量：" + bullets.size());
        System.out.println("子弹中心坐标：X=" + centerX + " Y=" + centerY);

        return bullets;
    }
}
