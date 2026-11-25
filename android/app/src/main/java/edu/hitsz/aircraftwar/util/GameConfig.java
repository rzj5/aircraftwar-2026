package edu.hitsz.aircraftwar.util;

/**
 * 虚拟坐标系常量：桌面包的 512x768 逻辑分辨率。
 * 实际渲染时按 Surface 真实宽高等比缩放（对应实验2 p24 第③个坑：不写死像素）。
 */
public final class GameConfig {
    public static final int VIRTUAL_WIDTH = 512;
    public static final int VIRTUAL_HEIGHT = 768;
    private GameConfig() {
    }
}
