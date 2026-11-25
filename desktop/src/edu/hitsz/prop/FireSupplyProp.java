package edu.hitsz.prop;

import edu.hitsz.aircraft.HeroAircraft;
import edu.hitsz.application.ImageManager;
import edu.hitsz.strategy.ScatterStrategy;
import edu.hitsz.strategy.SingleStraightStrategy;

import javax.swing.*;

public class FireSupplyProp extends BaseProp {
    // 道具持续时间
    private static final long BUFF_DURATION = 5000L;
    // 恢复线程，用于处理「重复拾取刷新计时」（volatile保证多线程可见性）
    private volatile Thread recoveryThread;
    // 默认单排直射策略（和你原来的DEFAULT_STRATEGY完全匹配）
    private static final SingleStraightStrategy DEFAULT_STRATEGY = new SingleStraightStrategy(30, -12, true);

    public FireSupplyProp(int locationX, int locationY, int speedX, int speedY) {
        super(locationX, locationY, speedX, speedY);
        this.image = ImageManager.FIRE_SUPPLY_PROP_IMAGE;
    }

    @Override
    public void active(HeroAircraft heroAircraft) {
        System.out.println("【火力道具】散射弹道已激活，持续5秒");

        // 1. 处理重复拾取：中断旧线程，重置计时
        if (recoveryThread != null && recoveryThread.isAlive()) {
            recoveryThread.interrupt();
            System.out.println("【火力道具】重复拾取，旧计时任务已取消，重新计时");
        }

        // 2. 切换为散射弹道
        heroAircraft.setShootStrategy(new ScatterStrategy(30, -12, true, 3));

        // 3. 创建并启动新的恢复线程
        recoveryThread = new Thread(() -> {
            try {
                // 等待道具持续时间
                Thread.sleep(BUFF_DURATION);

                // 4. 时间到后，在Swing EDT线程中恢复默认策略（保证线程安全）
                SwingUtilities.invokeLater(() -> {
                    // 先判断飞机是否有效，防止游戏结束后飞机被销毁导致报错
                    if (heroAircraft.isValid()) {
                        heroAircraft.setShootStrategy(DEFAULT_STRATEGY);
                        System.out.println("【火力道具】散射弹道到期，已恢复默认单排");
                    }
                });

            } catch (InterruptedException e) {
                // 线程被中断，说明玩家再次拾取了道具，直接退出不执行恢复
                Thread.currentThread().interrupt();
                return;
            }
        }, "FireSupplyProp-Recovery-Thread-" + System.currentTimeMillis());

        recoveryThread.start();
    }
}
