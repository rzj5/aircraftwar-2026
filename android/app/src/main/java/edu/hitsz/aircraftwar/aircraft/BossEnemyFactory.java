package edu.hitsz.aircraftwar.aircraft;

import edu.hitsz.aircraftwar.image.BitmapManager;
import edu.hitsz.aircraftwar.util.GameConfig;

public class BossEnemyFactory extends EnemyFactory {
    @Override
    public AbstractAircraft createEnemy() {
        int width = BitmapManager.BOSS_ENEMY.getWidth();
        int height = BitmapManager.BOSS_ENEMY.getHeight();
        int locationX = GameConfig.VIRTUAL_WIDTH / 2 - width / 2;
        int locationY = -height;
        return new BossEnemy(locationX, locationY, 3, 3, 150);
    }
}
