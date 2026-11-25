package edu.hitsz.aircraft;

import edu.hitsz.application.ImageManager;
import edu.hitsz.application.Main;

/**
 * 超级Boss敌机工厂：创建SuperBoss实例
 * 格式完全和MobEnemyFactory保持一致，满足「垂直不动，水平移动」
 */
public class SuperBossFactory extends EnemyFactory {
    private double hpMultiplier;

    // 无参构造（兼容你原来的代码）
    public SuperBossFactory() {
        this.hpMultiplier = 1.0;
    }

    // 👇【新增】带血量倍率的构造方法
    public SuperBossFactory(double hpMultiplier) {
        this.hpMultiplier = hpMultiplier;
    }

    @Override
    public AbstractAircraft createEnemy() {
        // 把倍率传给SuperBoss，替换你原来的固定血量
        // 比如你原来的Boss血量是300，就改成 300 * hpMultiplier
        return new SuperBoss(
                Main.WINDOW_WIDTH / 2, 100,
                0, 1,
                (int) (300 * hpMultiplier) // 这里改成你项目里Boss的原始血量
        );
    }
}
