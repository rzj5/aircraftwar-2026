package edu.hitsz.prop;

import edu.hitsz.aircraft.HeroAircraft;
import edu.hitsz.application.ImageManager;
import edu.hitsz.strategy.RingShootStrategy;
import edu.hitsz.strategy.SingleStraightStrategy;


import javax.swing.*;

public class FirePlusSupplyProp extends BaseProp {
    // 增强火力道具持续时间
    private static final long BUFF_DURATION = 5000L;
    // 恢复线程，用于处理「重复拾取刷新计时」（volatile保证多线程可见性）
    private volatile Thread recoveryThread;
    // 默认单排直射策略（参数和你的SingleStraightStrategy完全匹配）
    private static final SingleStraightStrategy DEFAULT_STRATEGY = new SingleStraightStrategy(30, -12, true);

    public FirePlusSupplyProp(int locationX, int locationY, int speedX, int speedY) {
        super(locationX, locationY, speedX, speedY);
        // 设置增强火力道具的图片（需和ImageManager中的常量对应）
        this.image = ImageManager.FIRE_PLUS_PROP_IMAGE;
    }

    @Override
    public void active(HeroAircraft heroAircraft) {
        System.out.println("【增强火力道具】环形散射弹道已激活，持续10秒");

        // 1. 处理重复拾取：中断旧线程，重置计时，实现“重复拾取刷新计时”
        if (recoveryThread != null && recoveryThread.isAlive()) {
            recoveryThread.interrupt();
            System.out.println("【增强火力道具】重复拾取，旧计时任务已取消，重新计时");
        }

        // 2. 修正参数：补全RingShootStrategy的第4个参数（子弹数量，和你的构造器完全匹配）
        heroAircraft.setShootStrategy(new RingShootStrategy(30, -12, true, 8));

        // 3. 创建并启动恢复线程，到期恢复默认策略
        recoveryThread = new Thread(() -> {
            try {
                // 等待道具持续时间
                Thread.sleep(BUFF_DURATION);

                // 4. 时间到后，在Swing EDT线程中恢复策略（保证线程安全）
                SwingUtilities.invokeLater(() -> {
                    // 先判断飞机是否有效，防止游戏结束后飞机被销毁报错
                    if (heroAircraft.isValid()) {
                        heroAircraft.setShootStrategy(DEFAULT_STRATEGY);
                        System.out.println("【增强火力道具】环形散射弹道到期，已恢复默认单排");
                    }
                });

            } catch (InterruptedException e) {
                // 线程被中断（重复拾取道具），重置中断状态即可，无需多余return
                Thread.currentThread().interrupt();
            }
        }, "FirePlusSupplyProp-Recovery-Thread-" + System.currentTimeMillis());

        recoveryThread.start();
    }
}
