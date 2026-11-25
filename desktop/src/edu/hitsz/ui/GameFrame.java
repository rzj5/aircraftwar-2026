package edu.hitsz.ui;

import javax.swing.*;
import java.awt.*;

/**
 * 游戏主窗口，用CardLayout管理所有界面
 * 对应指导书的CardLayoutDemo
 */
public class GameFrame extends JFrame {
    // 卡片布局管理器
    public static final CardLayout cardLayout = new CardLayout(0, 0);
    // 卡片容器面板
    public static final JPanel cardPanel = new JPanel(cardLayout);

    // 界面名称常量
    public static final String MENU_PANEL = "menu";
    public static final String GAME_PANEL = "game";
    public static final String RANK_PANEL = "rank";

    // 单例模式，全局只有一个主窗口
    private static GameFrame instance;

    private GameFrame() {
        // 窗口基础设置
        setTitle("飞机大战");
        setSize(512, 768); // 对应你游戏窗口的大小
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // 窗口居中
        setResizable(false);

        // 把卡片容器添加到窗口
        add(cardPanel);
    }

    // 获取单例
    public static GameFrame getInstance() {
        if (instance == null) {
            instance = new GameFrame();
        }
        return instance;
    }

    // 切换界面的方法，全局都能调用
    public void showPanel(String panelName) {
        cardLayout.show(cardPanel, panelName);
    }
}
