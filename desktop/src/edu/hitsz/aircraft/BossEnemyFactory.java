package edu.hitsz.aircraft;

import edu.hitsz.application.ImageManager;
import edu.hitsz.application.Main;

/**
 * 王牌（Boss）敌机工厂：创建BossEnemy实例
 */
public class BossEnemyFactory extends EnemyFactory {
    @Override
    public AbstractAircraft createEnemy() {
        int width = ImageManager.BOSS_ENEMY_IMAGE.getWidth();
        int height = ImageManager.BOSS_ENEMY_IMAGE.getHeight();

        // Boss机居中生成
        int locationX = Main.WINDOW_WIDTH / 2 - width / 2;
        int locationY = -height;

        BossEnemy enemy = new BossEnemy(locationX, locationY, 3, 3, 150);

        return enemy;
    }
}
