package edu.hitsz.aircraftwar.aircraft;

import edu.hitsz.aircraftwar.image.BitmapManager;

public class ElitePlusEnemyFactory extends EnemyFactory {
    @Override
    public AbstractAircraft createEnemy() {
        int width = BitmapManager.ELITE_PLUS_ENEMY.getWidth();
        int height = BitmapManager.ELITE_PLUS_ENEMY.getHeight();
        int[] loc = generateRandomLocation(width, height);
        int speedX = (int) (Math.random() * 7) - 3;
        return new ElitePlusEnemy(loc[0], loc[1], speedX, 6, 50);
    }
}
