package edu.hitsz.aircraftwar.aircraft;

import edu.hitsz.aircraftwar.image.BitmapManager;

public class EliteEnemyFactory extends EnemyFactory {
    @Override
    public AbstractAircraft createEnemy() {
        int width = BitmapManager.ELITE_ENEMY.getWidth();
        int height = BitmapManager.ELITE_ENEMY.getHeight();
        int[] loc = generateRandomLocation(width, height);
        int speedX = (int) (Math.random() * 5) - 2;
        return new EliteEnemy(loc[0], loc[1], speedX, 5, 30);
    }
}
