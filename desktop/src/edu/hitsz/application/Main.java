package edu.hitsz.application;

import edu.hitsz.aircraft.HeroAircraft;
import edu.hitsz.ui.GameFrame;
import edu.hitsz.ui.StartMenuPanel;

import javax.swing.*;
import java.awt.*;

/**
 * 程序入口
 * @author hitsz
 */
public class Main {
    // 保留你原来的窗口常量，其他代码里用到的地方不用改
    public static final int WINDOW_WIDTH = 512;
    public static final int WINDOW_HEIGHT = 768;

    public static void main(String[] args) {
        System.out.println("Hello Aircraft War");
        HeroAircraft.getInstance(
                WINDOW_WIDTH / 2,    // 初始X坐标：水平居中
                WINDOW_HEIGHT - 100, // 初始Y坐标：屏幕底部上方100像素
                0,                   // 初始横向速度（英雄机由键盘控制，设为0）
                0,                   // 初始纵向速度（设为0）
                100                  // 初始血量（和你项目里的默认值一致，比如100）
        );
        // 【核心修改】用全局单例的GameFrame管理主窗口，替代原来的临时JFrame
        GameFrame frame = GameFrame.getInstance();

        // 把难度选择界面添加到卡片容器，作为启动后的第一个界面
        GameFrame.cardPanel.add(new StartMenuPanel().getMainPanel(), GameFrame.MENU_PANEL);

        // 显示难度选择菜单，而不是直接启动游戏
        frame.showPanel(GameFrame.MENU_PANEL);

        // 显示窗口
        frame.setVisible(true);
    }
}
