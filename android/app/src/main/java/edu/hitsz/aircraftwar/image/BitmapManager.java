package edu.hitsz.aircraftwar.image;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import edu.hitsz.aircraftwar.R;

/**
 * 图片资源管理：一次性 decode 并缓存全部 Bitmap（实验2 p24 第②个坑：不要每帧重复加载）。
 * 对应桌面版 edu.hitsz.application.ImageManager。
 */
public final class BitmapManager {
    public static Bitmap BG_EASY;
    public static Bitmap BG_NORMAL;
    public static Bitmap BG_HARD;

    public static Bitmap HERO;
    public static Bitmap MOB_ENEMY;
    public static Bitmap ELITE_ENEMY;
    public static Bitmap ELITE_PLUS_ENEMY;
    public static Bitmap BOSS_ENEMY;
    public static Bitmap SUPER_BOSS;
    public static Bitmap HERO_BULLET;
    public static Bitmap ENEMY_BULLET;

    public static Bitmap HP_PROP;
    public static Bitmap BOMB_PROP;
    public static Bitmap FIRE_PROP;
    public static Bitmap FIRE_PLUS_PROP;
    public static Bitmap FREEZE_PROP;

    private static boolean loaded = false;

    private BitmapManager() {
    }

    public static synchronized void load(Context context) {
        if (loaded) {
            return;
        }
        Context app = context.getApplicationContext();
        BG_EASY = decode(app, R.drawable.bg_easy);
        BG_NORMAL = decode(app, R.drawable.bg_normal);
        BG_HARD = decode(app, R.drawable.bg_hard);

        HERO = decode(app, R.drawable.hero);
        MOB_ENEMY = decode(app, R.drawable.mob);
        ELITE_ENEMY = decode(app, R.drawable.elite);
        ELITE_PLUS_ENEMY = decode(app, R.drawable.elite_plus);
        BOSS_ENEMY = decode(app, R.drawable.elite_pro);
        SUPER_BOSS = decode(app, R.drawable.boss);

        HERO_BULLET = decode(app, R.drawable.bullet_hero);
        ENEMY_BULLET = decode(app, R.drawable.bullet_enemy);

        HP_PROP = decode(app, R.drawable.prop_blood);
        BOMB_PROP = decode(app, R.drawable.prop_bomb);
        FIRE_PROP = decode(app, R.drawable.prop_bullet);
        FIRE_PLUS_PROP = decode(app, R.drawable.prop_bullet_plus);
        FREEZE_PROP = decode(app, R.drawable.prop_freeze);

        loaded = true;
    }

    private static Bitmap decode(Context context, int resId) {
        return BitmapFactory.decodeResource(context.getResources(), resId);
    }
}
