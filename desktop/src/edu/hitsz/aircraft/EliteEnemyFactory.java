package edu.hitsz.aircraft;

import edu.hitsz.application.ImageManager;

/**
 * 精英敌机工厂：创建EliteEnemy实例
 */
public class EliteEnemyFactory extends EnemyFactory {
    @Override
    public AbstractAircraft createEnemy() {
        int width = ImageManager.ELITE_ENEMY_IMAGE.getWidth();
        int height = ImageManager.ELITE_ENEMY_IMAGE.getHeight();
        int[] loc = generateRandomLocation(width, height);

        // 水平随机速度-2~2，实现左右移动
        int speedX = (int) (Math.random() * 5) - 2;
        EliteEnemy enemy = new EliteEnemy(loc[0], loc[1], speedX, 5, 30);

        // 图片绑定移到EliteEnemy构造方法
        return enemy;
    }
}
