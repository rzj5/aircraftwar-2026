package edu.hitsz.application;

import edu.hitsz.aircraft.HeroAircraft;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * 英雄机控制类
 * 监听鼠标，控制英雄机的移动
 * @author hitsz
 */
public class HeroController {
    private final JPanel gamePanel; // 用JPanel替代Game，GameTemplate是它的子类
    private final HeroAircraft heroAircraft;
    private MouseAdapter mouseAdapter;

    // 新构造方法：接收游戏面板和英雄机实例
    public HeroController(JPanel gamePanel, HeroAircraft heroAircraft) {
        this.gamePanel = gamePanel;
        this.heroAircraft = heroAircraft;

        // 初始化仅拖拽的监听器
        initMouseAdapter();
    }

    private void initMouseAdapter() {
        mouseAdapter = new MouseAdapter() {
            // 👇 只保留你原来的拖拽逻辑，和之前的代码完全一样
            @Override
            public void mouseDragged(MouseEvent e) {
                super.mouseDragged(e);
                int x = e.getX();
                int y = e.getY();

                // 防止英雄机移出边界（和你原来的判断逻辑完全一致）
                if (x < 0 || x > Main.WINDOW_WIDTH || y < 0 || y > Main.WINDOW_HEIGHT) {
                    return;
                }

                // 设置英雄机位置
                heroAircraft.setLocation(x, y);
            }
        };

        // 把监听器绑定到游戏面板（和你原来的逻辑一样）
        gamePanel.addMouseListener(mouseAdapter);
        gamePanel.addMouseMotionListener(mouseAdapter);
    }
}
