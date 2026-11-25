package edu.hitsz.aircraft;

import edu.hitsz.application.ImageManager;

/**
 * 普通敌机工厂：创建MobEnemy实例
 */
public class MobEnemyFactory extends EnemyFactory {
    @Override
    public AbstractAircraft createEnemy() {
        // 获取敌机图片宽高，生成合法坐标
        int width = ImageManager.MOB_ENEMY_IMAGE.getWidth();
        int height = ImageManager.MOB_ENEMY_IMAGE.getHeight();
        int[] loc = generateRandomLocation(width, height);

        // 普通敌机：水平不动，垂直下落，血量10
        MobEnemy enemy = new MobEnemy(loc[0], loc[1], 0, 5, 10);
        return enemy;
    }
}
