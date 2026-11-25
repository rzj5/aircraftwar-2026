package edu.hitsz.aircraftwar.aircraft;

import edu.hitsz.aircraftwar.util.GameConfig;

public class SuperBossFactory extends EnemyFactory {
    private double hpMultiplier;

    public SuperBossFactory() {
        this.hpMultiplier = 1.0;
    }

    public SuperBossFactory(double hpMultiplier) {
        this.hpMultiplier = hpMultiplier;
    }

    @Override
    public AbstractAircraft createEnemy() {
        return new SuperBoss(GameConfig.VIRTUAL_WIDTH / 2, 100, 0, 1, (int) (300 * hpMultiplier));
    }
}
