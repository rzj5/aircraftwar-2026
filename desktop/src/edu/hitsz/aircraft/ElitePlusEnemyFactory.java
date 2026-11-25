package edu.hitsz.aircraft;

import edu.hitsz.application.ImageManager;

/**
 * 精锐敌机工厂：创建ElitePlusEnemy实例
 */
public class ElitePlusEnemyFactory extends EnemyFactory {
    @Override
    public AbstractAircraft createEnemy() {
        int width = ImageManager.ELITE_PLUS_ENEMY_IMAGE.getWidth();
        int height = ImageManager.ELITE_PLUS_ENEMY_IMAGE.getHeight();
        int[] loc = generateRandomLocation(width, height);

        // 水平随机速度-3~3
        int speedX = (int) (Math.random() * 7) - 3;
        ElitePlusEnemy enemy = new ElitePlusEnemy(loc[0], loc[1], speedX, 6, 50);

        return enemy;
    }
}
