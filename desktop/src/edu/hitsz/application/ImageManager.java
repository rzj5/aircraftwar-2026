package edu.hitsz.application;


import edu.hitsz.aircraft.*;
import edu.hitsz.bullet.EnemyBullet;
import edu.hitsz.bullet.HeroBullet;
import edu.hitsz.prop.*;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * 综合管理图片的加载，访问
 * 提供图片的静态访问方法
 * @author hitsz
 */
public class ImageManager {

    /**
     * 类名-图片 映射，存储各基类的图片 <br>
     * 可使用 CLASSNAME_IMAGE_MAP.get( obj.getClass().getName() ) 获得 obj 所属基类对应的图片
     */
    // 3个难度对应的背景图，和你的图片文件完全对应
    public static BufferedImage BG_EASY;    // 简单模式 → bg.jpg
    public static BufferedImage BG_NORMAL;  // 普通模式 → bg2.jpg
    public static BufferedImage BG_HARD;    // 困难模式 → bg3.jpg

    private static final Map<String, BufferedImage> CLASSNAME_IMAGE_MAP = new HashMap<>();

    public static BufferedImage BACKGROUND_IMAGE;
    public static BufferedImage HERO_IMAGE;
    public static BufferedImage HERO_BULLET_IMAGE;
    public static BufferedImage ENEMY_BULLET_IMAGE;
    public static BufferedImage MOB_ENEMY_IMAGE;
    // ====================== 新增：敌机图片常量 ======================
    public static BufferedImage ELITE_ENEMY_IMAGE;
    public static BufferedImage ELITE_PLUS_ENEMY_IMAGE;
    public static BufferedImage BOSS_ENEMY_IMAGE;
    public static BufferedImage SUPER_BOSS_IMAGE;

    // ====================== 新增：道具图片常量 ======================
    public static BufferedImage HP_PROP_IMAGE;
    public static BufferedImage BOMB_PROP_IMAGE;
    public static BufferedImage FIRE_SUPPLY_PROP_IMAGE;
    public static BufferedImage FIRE_PLUS_PROP_IMAGE;
    public static BufferedImage FREEZE_PROP_IMAGE;


    static {
        try {
            // 加载3个难度的背景图，路径和你的images文件夹对应
            BG_EASY = ImageIO.read(new FileInputStream("src/images/bg2.jpg"));
            BG_NORMAL = ImageIO.read(new FileInputStream("src/images/bg3.jpg"));
            BG_HARD = ImageIO.read(new FileInputStream("src/images/bg4.jpg"));

            BACKGROUND_IMAGE = ImageIO.read(new FileInputStream("src/images/bg.jpg"));

            HERO_IMAGE = ImageIO.read(new FileInputStream("src/images/hero.png"));
            MOB_ENEMY_IMAGE = ImageIO.read(new FileInputStream("src/images/mob.png"));
            HERO_BULLET_IMAGE = ImageIO.read(new FileInputStream("src/images/bullet_hero.png"));
            ENEMY_BULLET_IMAGE = ImageIO.read(new FileInputStream("src/images/bullet_enemy.png"));
            // ====================== 新增：敌机图片加载 ======================
            ELITE_ENEMY_IMAGE = ImageIO.read(new FileInputStream("src/images/elite.png"));
            ELITE_PLUS_ENEMY_IMAGE = ImageIO.read(new FileInputStream("src/images/elitePlus.png"));
            BOSS_ENEMY_IMAGE = ImageIO.read(new FileInputStream("src/images/elitePro.png"));
            SUPER_BOSS_IMAGE = ImageIO.read(new FileInputStream("src/images/boss.png"));

// ====================== 新增：道具图片加载 ======================
            HP_PROP_IMAGE = ImageIO.read(new FileInputStream("src/images/prop_blood.png"));
            BOMB_PROP_IMAGE = ImageIO.read(new FileInputStream("src/images/prop_bomb.png"));
            FIRE_SUPPLY_PROP_IMAGE = ImageIO.read(new FileInputStream("src/images/prop_bullet.png"));
            FIRE_PLUS_PROP_IMAGE = ImageIO.read(new FileInputStream("src/images/prop_bulletPlus.png"));
            FREEZE_PROP_IMAGE = ImageIO.read(new FileInputStream("src/images/prop_freeze.png"));


            CLASSNAME_IMAGE_MAP.put(HeroAircraft.class.getName(), HERO_IMAGE);
            CLASSNAME_IMAGE_MAP.put(MobEnemy.class.getName(), MOB_ENEMY_IMAGE);
            CLASSNAME_IMAGE_MAP.put(HeroBullet.class.getName(), HERO_BULLET_IMAGE);
            CLASSNAME_IMAGE_MAP.put(EnemyBullet.class.getName(), ENEMY_BULLET_IMAGE);
            // ====================== 新增：敌机类-图片映射 ======================
            CLASSNAME_IMAGE_MAP.put(EliteEnemy.class.getName(), ELITE_ENEMY_IMAGE);
            CLASSNAME_IMAGE_MAP.put(ElitePlusEnemy.class.getName(), ELITE_PLUS_ENEMY_IMAGE);
            CLASSNAME_IMAGE_MAP.put(BossEnemy.class.getName(), BOSS_ENEMY_IMAGE);
            CLASSNAME_IMAGE_MAP.put(SuperBoss.class.getName(), SUPER_BOSS_IMAGE);

// ====================== 新增：道具类-图片映射 ======================
            CLASSNAME_IMAGE_MAP.put(HpProp.class.getName(), HP_PROP_IMAGE);
            CLASSNAME_IMAGE_MAP.put(BombProp.class.getName(), BOMB_PROP_IMAGE);
            CLASSNAME_IMAGE_MAP.put(FireSupplyProp.class.getName(), FIRE_SUPPLY_PROP_IMAGE);
            CLASSNAME_IMAGE_MAP.put(FirePlusSupplyProp.class.getName(), FIRE_PLUS_PROP_IMAGE);
            CLASSNAME_IMAGE_MAP.put(FreezeProp.class.getName(), FREEZE_PROP_IMAGE);


        } catch (IOException e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }

    public static BufferedImage get(String className){
        return CLASSNAME_IMAGE_MAP.get(className);
    }

    public static BufferedImage get(Object obj){
        if (obj == null){
            return null;
        }
        return get(obj.getClass().getName());
    }

}
