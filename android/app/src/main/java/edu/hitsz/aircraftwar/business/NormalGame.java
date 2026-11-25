package edu.hitsz.aircraftwar.business;

import android.content.Context;
import android.os.Handler;

import edu.hitsz.aircraftwar.image.BitmapManager;

public class NormalGame extends BaseGame {
    public NormalGame(Context context, Handler mainHandler) {
        super(context, "normal", mainHandler);
        enemySpawnCycle = 20;
        shootCycle = 20;
    }

    @Override
    protected void initBackground() {
        setBackgroundBitmap(BitmapManager.BG_NORMAL);
    }

    @Override
    public boolean allowSuperBoss() {
        return true;
    }

    @Override
    public void increaseDifficulty() {
        if (enemySpawnCycle > 10) {
            enemySpawnCycle -= 0.05;
        }
    }
}
