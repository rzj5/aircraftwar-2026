package edu.hitsz.application;

import java.awt.image.BufferedImage;

public class EasyGame extends BaseGame {
    public EasyGame() {
        super("easy");
        enemySpawnCycle = 30;
        shootCycle = 25;
    }

    @Override
    protected void initBackground() {
        background = ImageManager.BG_EASY;
    }

    @Override
    public boolean allowSuperBoss() {
        return false;
    }

    @Override
    public void increaseDifficulty() {
        // 简单模式：不提升难度
    }
}