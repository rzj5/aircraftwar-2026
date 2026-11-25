package edu.hitsz.aircraftwar.aircraft;

import edu.hitsz.aircraftwar.image.BitmapManager;

public class MobEnemyFactory extends EnemyFactory {
    @Override
    public AbstractAircraft createEnemy() {
        int width = BitmapManager.MOB_ENEMY.getWidth();
        int height = BitmapManager.MOB_ENEMY.getHeight();
        int[] loc = generateRandomLocation(width, height);
        return new MobEnemy(loc[0], loc[1], 0, 5, 10);
    }
}
