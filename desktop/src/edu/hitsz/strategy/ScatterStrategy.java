package edu.hitsz.strategy;

import edu.hitsz.aircraft.AbstractAircraft;
import edu.hitsz.bullet.BaseBullet;
import edu.hitsz.bullet.EnemyBullet;
import edu.hitsz.bullet.HeroBullet;
import java.util.ArrayList;
import java.util.List;

/**
 * 散射策略
 * 对应王牌敌机、火力道具生效后的英雄机
 */
public class ScatterStrategy implements ShootStrategy {
    // 子弹伤害
    private final int power;
    // 子弹Y方向速度
    private final int speedY;
    // 是否是英雄机的子弹（区分子弹类型和飞行方向）
    private final boolean isHero;
    // 散射的子弹数量
    private final int bulletCount;

    /**
     * 构造方法
     * @param power 子弹伤害
     * @param speedY 子弹Y方向速度（正数向下，负数向上）
     * @param isHero 是否是英雄机的子弹
     * @param bulletCount 散射的子弹总数
     */
    public ScatterStrategy(int power, int speedY, boolean isHero, int bulletCount) {
        this.power = power;
        this.speedY = speedY;
        this.isHero = isHero;
        this.bulletCount = bulletCount;
    }

    @Override
    public List<BaseBullet> shoot(AbstractAircraft aircraft) {
        List<BaseBullet> bullets = new ArrayList<>();

        // ========== 完全适配你的中心点坐标逻辑 ==========
        // 1. 子弹基准X坐标：直接用飞机的中心点（已经是居中的）
        int baseX = aircraft.getLocationX();
        // 2. 子弹基准Y坐标：适配中心点，英雄机从顶部发射，敌机从底部发射
        int baseY;
        int halfHeight = aircraft.getHeight() / 2;
        if (isHero) {
            baseY = aircraft.getLocationY() - halfHeight;
        } else {
            baseY = aircraft.getLocationY() + halfHeight;
        }

        // 3. 散射水平速度间隔，实现扇形散开（数值越小，扇形越窄）
        int speedXStep = 4;
        // ==============================================

        // 循环生成多颗散射子弹
        for (int i = 0; i < bulletCount; i++) {
            // 计算每颗子弹的水平速度，实现左右对称的扇形
            int speedX = (i - (bulletCount - 1) / 2) * speedXStep;

            // 生成子弹
            if (isHero) {
                bullets.add(new HeroBullet(baseX, baseY, speedX, speedY, power));
            } else {
                bullets.add(new EnemyBullet(baseX, baseY, speedX, speedY, power));
            }
        }

        // 调试日志，运行时看控制台有没有输出
        System.out.println("===== 执行散射策略 =====");
        System.out.println("生成子弹数量：" + bullets.size());
        System.out.println("子弹基准坐标：X=" + baseX + " Y=" + baseY);

        return bullets;
    }
}
