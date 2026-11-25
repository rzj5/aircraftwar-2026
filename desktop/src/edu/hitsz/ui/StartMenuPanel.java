package edu.hitsz.ui;

import edu.hitsz.application.BaseGame;
import edu.hitsz.application.EasyGame;
import edu.hitsz.application.NormalGame;
import edu.hitsz.application.HardGame;

import javax.swing.*;
import java.awt.*;

public class StartMenuPanel {
    private JPanel mainPanel;
    private JButton easyButton;
    private JButton normalButton;
    private JButton hardButton;

    public StartMenuPanel() {
        // 用程序化初始化替代空的 IDEA UI Designer 桩方法，实际创建面板与按钮
        initComponents();

        // 统一设置按钮字体，优化显示效果
        Font buttonFont = new Font("宋体", Font.PLAIN, 18);
        easyButton.setFont(buttonFont);
        normalButton.setFont(buttonFont);
        hardButton.setFont(buttonFont);

        // 绑定按钮点击事件
        initButtonEvents();
    }

    // 初始化按钮事件，和难度一一对应
    private void initButtonEvents() {
        // 简单模式
        easyButton.addActionListener(e -> startGame("easy"));
        // 普通模式
        normalButton.addActionListener(e -> startGame("normal"));
        // 困难模式
        hardButton.addActionListener(e -> startGame("hard"));
    }
    // 启动游戏的逻辑
    private void startGame(String difficulty) {
        GameFrame frame = GameFrame.getInstance();
        // 窗口大小和GameFrame保持一致：512x768（和你GameFrame里的setSize参数完全匹配）
        int width = 512;
        int height = 768;
        BaseGame game;

        // 根据传入的难度字符串，创建对应的游戏实例
        switch (difficulty) {
            case "easy":
                game = new EasyGame();
                break;
            case "normal":
                game = new NormalGame();
                break;
            case "hard":
                game = new HardGame();
                break;
            default:
                // 防止参数异常，默认创建普通难度
                game = new NormalGame();
        }

        // 把游戏面板加到卡片容器（和你原来的代码逻辑完全一致）
        GameFrame.cardPanel.add(game, GameFrame.GAME_PANEL);
        // 切换到游戏界面
        frame.showPanel(GameFrame.GAME_PANEL);
        // 启动游戏主循环
        new Thread(() -> {
            game.action();
        }).start();
    }


    // 给卡片布局提供主面板
    public JPanel getMainPanel() {
        return mainPanel;
    }

    // 创建主菜单面板：三个按钮纵向排列
    private void initComponents() {
        mainPanel = new JPanel(new GridLayout(3, 1, 0, 18));
        mainPanel.setPreferredSize(new Dimension(280, 240));
        mainPanel.setBackground(new Color(245, 247, 250));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(24, 60, 24, 60));

        easyButton = new JButton("简单模式");
        normalButton = new JButton("普通模式");
        hardButton = new JButton("困难模式");
        mainPanel.add(easyButton);
        mainPanel.add(normalButton);
        mainPanel.add(hardButton);
    }
}
