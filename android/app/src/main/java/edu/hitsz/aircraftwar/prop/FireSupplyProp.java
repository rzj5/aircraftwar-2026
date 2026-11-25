package edu.hitsz.aircraftwar.prop;

import edu.hitsz.aircraftwar.aircraft.HeroAircraft;
import edu.hitsz.aircraftwar.strategy.ScatterStrategy;
import edu.hitsz.aircraftwar.strategy.SingleStraightStrategy;

/**
 * 火力道具：5 秒内散射三发。
 * 桌面版用 SwingUtilities.invokeLater 回 EDT；Android 侧策略切换只改字段，
 * 由游戏线程读取，无需切回主线程（主线程只负责 UI）。
 */
public class FireSupplyProp extends BaseProp {
    private static final long BUFF_DURATION = 5000L;
    private static final SingleStraightStrategy DEFAULT_STRATEGY = new SingleStraightStrategy(30, -12, true);
    private volatile Thread recoveryThread;

    public FireSupplyProp(int locationX, int locationY, int speedX, int speedY) {
        super(locationX, locationY, speedX, speedY);
        this.image = edu.hitsz.aircraftwar.image.BitmapManager.FIRE_PROP;
    }

    @Override
    public void active(HeroAircraft heroAircraft) {
        if (recoveryThread != null && recoveryThread.isAlive()) {
            recoveryThread.interrupt();
        }
        heroAircraft.setShootStrategy(new ScatterStrategy(30, -12, true, 3));
        recoveryThread = new Thread(() -> {
            try {
                Thread.sleep(BUFF_DURATION);
                if (heroAircraft.isValid()) {
                    heroAircraft.setShootStrategy(DEFAULT_STRATEGY);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }, "fire-supply-recovery");
        recoveryThread.start();
    }
}
