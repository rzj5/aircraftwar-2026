package edu.hitsz.application;

import edu.hitsz.aircraft.SuperBossFactory;

public class HardGame extends BaseGame {
    private double bossHpMul = 1.0;

    public HardGame() {
        super("hard");
        enemySpawnCycle = 12;
        shootCycle = 15;
    }

    @Override
    protected void initBackground() {
        background = ImageManager.BG_HARD;
    }

    @Override
    public boolean allowSuperBoss() {
        return true;
    }

    @Override
    public void increaseDifficulty() {
        if (enemySpawnCycle > 6) enemySpawnCycle -= 0.08;
        if (shootCycle < 30) shootCycle += 0.05;
    }

    @Override
    public void action() {
        superBossFactory = new SuperBossFactory(bossHpMul += 0.2);
        super.action();
    }
}