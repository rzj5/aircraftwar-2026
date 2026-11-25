package edu.hitsz.aircraftwar.business;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

import edu.hitsz.aircraftwar.aircraft.SuperBossFactory;
import edu.hitsz.aircraftwar.image.BitmapManager;

public class HardGame extends BaseGame {
    private static final String TAG = "HardGame";
    private double bossHpMul = 1.0;

    public HardGame(Context context, Handler mainHandler) {
        super(context, "hard", mainHandler);
        enemySpawnCycle = 12;
        shootCycle = 15;
        // 与桌面版 HardGame.action() 一致：每开一局提升一次超级 Boss 血量倍率
        applyBossHpMultiplier();
        Log.i(TAG, "困难模式开局，超级Boss血量倍率=" + bossHpMul);
    }

    @Override
    protected void initBackground() {
        setBackgroundBitmap(BitmapManager.BG_HARD);
    }

    @Override
    public boolean allowSuperBoss() {
        return true;
    }

    @Override
    public void increaseDifficulty() {
        if (enemySpawnCycle > 6) {
            enemySpawnCycle -= 0.08;
        }
        if (shootCycle < 30) {
            shootCycle += 0.05;
        }
    }

    /** 困难模式每局叠加 boss 血量倍率（与桌面版一致） */
    public void applyBossHpMultiplier() {
        bossHpMul += 0.2;
        superBossFactory = new SuperBossFactory(bossHpMul);
    }
}
