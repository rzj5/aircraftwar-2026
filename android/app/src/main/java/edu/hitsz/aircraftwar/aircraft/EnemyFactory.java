package edu.hitsz.aircraftwar.aircraft;

import edu.hitsz.aircraftwar.util.GameConfig;

public abstract class EnemyFactory {

    public abstract AbstractAircraft createEnemy();

    protected int[] generateRandomLocation(int width, int height) {
        int locationX = (int) (Math.random() * (GameConfig.VIRTUAL_WIDTH - width));
        int locationY = -height;
        return new int[]{locationX, locationY};
    }
}
