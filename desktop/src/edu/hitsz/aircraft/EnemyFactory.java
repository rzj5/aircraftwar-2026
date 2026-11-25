package edu.hitsz.aircraft;

import edu.hitsz.application.Main;

/**
 * 敌机抽象工厂类（工厂方法模式的抽象创建者）
 * 定义所有敌机工厂的统一规范，子类实现具体敌机的创建
 */
public abstract class EnemyFactory {

    /**
     * 抽象方法：创建具体敌机实例，由子类实现
     * @return 具体的敌机对象
     */
    public abstract AbstractAircraft createEnemy();

    /**
     * 通用工具方法：生成敌机随机初始坐标（屏幕顶部外，X轴随机，避免出界）
     * @param width 敌机图片宽度
     * @param height 敌机图片高度
     * @return 坐标数组：[locationX, locationY]
     */
    protected int[] generateRandomLocation(int width, int height) {
        // X轴：屏幕内随机，保证敌机不会左右出界
        int locationX = (int) (Math.random() * (Main.WINDOW_WIDTH - width));
        // Y轴：屏幕顶部外，让敌机从上方飞入
        int locationY = -height;
        return new int[]{locationX, locationY};
    }
}
