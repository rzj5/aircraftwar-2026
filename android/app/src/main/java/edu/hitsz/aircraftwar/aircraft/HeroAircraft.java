package edu.hitsz.aircraftwar.aircraft;

import edu.hitsz.aircraftwar.image.BitmapManager;
import edu.hitsz.aircraftwar.strategy.SingleStraightStrategy;

/**
 * 英雄飞机：单例 + 由触屏输入控制（对应桌面版鼠标拖拽）。
 */
public class HeroAircraft extends AbstractAircraft {
    private static volatile HeroAircraft instance;

    private HeroAircraft(int locationX, int locationY, int speedX, int speedY, int hp) {
        super(locationX, locationY, speedX, speedY, hp);
        this.image = BitmapManager.HERO;
        this.shootStrategy = new SingleStraightStrategy(30, -12, true);
        this.maxHp = hp;
    }

    public static HeroAircraft getInstance(int locationX, int locationY, int speedX, int speedY, int hp) {
        if (instance == null) {
            synchronized (HeroAircraft.class) {
                if (instance == null) {
                    instance = new HeroAircraft(locationX, locationY, speedX, speedY, hp);
                }
            }
        }
        return instance;
    }

    public static HeroAircraft getInstance() {
        if (instance == null) {
            throw new RuntimeException("英雄机还未初始化！请先调用带参 getInstance 创建实例");
        }
        return instance;
    }

    @Override
    public void forward() {
        // 英雄机由触屏输入控制，不走自动位移
    }
}
