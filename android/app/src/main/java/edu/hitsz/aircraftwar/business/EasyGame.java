package edu.hitsz.aircraftwar.business;

import android.content.Context;
import android.os.Handler;

import edu.hitsz.aircraftwar.image.BitmapManager;

public class EasyGame extends BaseGame {
    public EasyGame(Context context, Handler mainHandler) {
        super(context, "easy", mainHandler);
        enemySpawnCycle = 30;
        shootCycle = 25;
    }

    @Override
    protected void initBackground() {
        setBackgroundBitmap(BitmapManager.BG_EASY);
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
