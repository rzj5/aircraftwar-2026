package edu.hitsz.aircraft;

import edu.hitsz.bullet.BaseBullet;
import edu.hitsz.bullet.HeroBullet;
import edu.hitsz.application.ImageManager;
import edu.hitsz.strategy.ShootStrategy;
import edu.hitsz.strategy.SingleStraightStrategy;

import java.util.LinkedList;
import java.util.List;

/**
 * 英雄飞机，游戏玩家操控
 * @author hitsz
 */
public class HeroAircraft extends AbstractAircraft {
    private int maxHp;
    // 1. 私有静态实例，volatile禁止指令重排，保证线程安全
    private static volatile HeroAircraft instance;

    // 2. 私有化构造方法，禁止外部new创建实例
    private HeroAircraft(int locationX, int locationY, int speedX, int speedY, int hp) {
        super(locationX, locationY, speedX, speedY, hp);
        // 绑定英雄机图片，避免空指针
        this.image = ImageManager.HERO_IMAGE;
// 设置默认射击策略：单排直射，伤害30，子弹向上飞（速度-12），英雄机子弹
        this.shootStrategy = new SingleStraightStrategy(30, -12, true);
        this.maxHp = hp;
    }

    // 3. 全局唯一的公有静态方法，获取单例实例
    public static HeroAircraft getInstance(int locationX, int locationY, int speedX, int speedY, int hp) {
        // 第一次检查：实例已创建就直接返回，不进入锁
        if (instance == null) {
            // 加类锁，保证多线程安全
            synchronized (HeroAircraft.class) {
                // 第二次检查：避免多线程同时进入第一层if，重复创建实例
                if (instance == null) {
                    instance = new HeroAircraft(locationX, locationY, speedX, speedY, hp);
                }
            }
        }
        return instance;
    }
    /**
     * 游戏运行中，获取已经创建好的英雄机单例
     * 道具生效时调用，不需要传坐标参数
     */
    public static HeroAircraft getInstance() {
        if (instance == null) {
            throw new RuntimeException("英雄机还未初始化！请先调用带参getInstance创建实例");
        }
        return instance;
    }


    //每次射击发射子弹数量
    private int shootNum = 1;

    //子弹威力
    private int power = 30;

    //子弹射击方向 (向上发射：-1，向下发射：1)
    private int direction = -1;
    public void setShootStrategy(ShootStrategy strategy) {
        this.shootStrategy = strategy;
    }


    @Override
    public void forward() {
        // 英雄机由鼠标控制，不通过forward函数移动
    }


}
