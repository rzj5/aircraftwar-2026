package edu.hitsz.aircraftwar.prop;

import edu.hitsz.aircraftwar.aircraft.HeroAircraft;
import edu.hitsz.aircraftwar.strategy.RingShootStrategy;
import edu.hitsz.aircraftwar.strategy.SingleStraightStrategy;

/**
 * 增强火力道具：5 秒内八发环射。
 */
public class FirePlusSupplyProp extends BaseProp {
    private static final long BUFF_DURATION = 5000L;
    private static final SingleStraightStrategy DEFAULT_STRATEGY = new SingleStraightStrategy(30, -12, true);
    private volatile Thread recoveryThread;

    public FirePlusSupplyProp(int locationX, int locationY, int speedX, int speedY) {
        super(locationX, locationY, speedX, speedY);
        this.image = edu.hitsz.aircraftwar.image.BitmapManager.FIRE_PLUS_PROP;
    }

    @Override
    public void active(HeroAircraft heroAircraft) {
        if (recoveryThread != null && recoveryThread.isAlive()) {
            recoveryThread.interrupt();
        }
        heroAircraft.setShootStrategy(new RingShootStrategy(30, -12, true, 8));
        recoveryThread = new Thread(() -> {
            try {
                Thread.sleep(BUFF_DURATION);
                if (heroAircraft.isValid()) {
                    heroAircraft.setShootStrategy(DEFAULT_STRATEGY);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }, "fire-plus-supply-recovery");
        recoveryThread.start();
    }
}
