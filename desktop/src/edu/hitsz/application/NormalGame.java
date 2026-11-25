package edu.hitsz.application;

public class NormalGame extends BaseGame {
    public NormalGame() {
        super("normal");
        enemySpawnCycle = 20;
        shootCycle = 20;
    }

    @Override
    protected void initBackground() {
        background = ImageManager.BG_NORMAL;
    }

    @Override
    public boolean allowSuperBoss() {
        return true;
    }

    @Override
    public void increaseDifficulty() {
        if (enemySpawnCycle > 10) enemySpawnCycle -= 0.05;
    }
}