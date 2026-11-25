package edu.hitsz.basic;

import edu.hitsz.aircraft.AbstractAircraft;
import edu.hitsz.application.Game;
import edu.hitsz.application.ImageManager;
import edu.hitsz.application.Main;
import java.awt.image.BufferedImage;

/**
 * 所有飞行物体的基类（飞机、子弹、道具）
 */
public abstract class AbstractFlyingObject {
    // 物体坐标
    protected int locationX;
    protected int locationY;
    // x,y轴移动速度
    protected int speedX;
    protected int speedY;
    // 图片，null 表示未设置
    protected BufferedImage image = null;
    // x轴长度，根据图片尺寸获得，-1 表示未设置
    protected int width = -1;
    // y轴长度，根据图片尺寸获得，-1 表示未设置
    protected int height = -1;
    // 有效（生存）标记，标记为 false 的对象会在下次刷新时清除
    protected boolean isValid = true;
    public void setValid(boolean valid) {
        this.isValid = valid;
    }


    public AbstractFlyingObject(int locationX, int locationY, int speedX, int speedY) {
        this.locationX = locationX;
        this.locationY = locationY;
        this.speedX = speedX;
        this.speedY = speedY;
    }

    /**
     * 可飞行对象根据速度移动
     * 若飞行对象触碰到横向边界，横向速度反向
     */
    public void forward() {
        // 1. 基础移动
        locationX += speedX;
        locationY += speedY;

        // ========== 【核心修正】适配中心点坐标的左右边界反弹 ==========
        // 计算飞机的左右边缘坐标（中心点±半宽）
        int leftEdge = locationX - width / 2;
        int rightEdge = locationX + width / 2;

        // 左边界：飞机左边缘碰到窗口左边界，反弹
        if (leftEdge <= 0) {
            locationX = width / 2; // 修正位置，避免卡进边界
            speedX = -speedX; // 横向速度取反，实现反弹
        }

        // 右边界：飞机右边缘碰到窗口右边界，反弹
        if (rightEdge >= Main.WINDOW_WIDTH) {
            locationX = Main.WINDOW_WIDTH - width / 2; // 修正位置，避免卡进边界
            speedX = -speedX; // 横向速度取反，实现反弹
        }
    }

    /**
     * 碰撞检测，当对方坐标进入我方范围，判定我方击中<br>
     * 对方与我方覆盖区域有交叉即判定撞击。
     * <br>
     * 非飞机对象区域：
     * 横向，[x - width/2, x + width/2]
     * 纵向，[y - height/2, y + height/2]
     * <br>
     * 飞机对象区域：
     * 横向，[x - width/2, x + width/2]
     * 纵向，[y - height/4, y + height/4]
     *
     * @param flyingObject 撞击对方
     * @return true: 我方被击中; false 我方未被击中
     */
    public boolean crash(AbstractFlyingObject flyingObject) {
        // 缩放因子，用于控制 y轴方向区域范围
        int factor = this instanceof AbstractAircraft ? 2 : 1; //我方
        int fFactor = flyingObject instanceof AbstractAircraft ? 2 : 1;//对方

        // 对方坐标、宽度、高度
        int x = flyingObject.getLocationX();
        int y = flyingObject.getLocationY();
        int fWidth = flyingObject.getWidth();
        int fHeight = flyingObject.getHeight();

        return x + (fWidth+this.getWidth())/2 > locationX
                && x - (fWidth+this.getWidth())/2 < locationX
                && y + ( fHeight/fFactor+this.getHeight()/factor )/2 > locationY
                && y - ( fHeight/fFactor+this.getHeight()/factor )/2 < locationY;
    }

    // ========== 【核心修复】补全缺失的getter/setter方法 ==========
    public int getLocationX() {
        return locationX;
    }

    public int getLocationY() {
        return locationY;
    }
    // 【新增】设置X坐标，解决SuperBoss找不到符号的核心问题
    public void setLocationX(int locationX) {
        this.locationX = locationX;
    }

    // 【新增】设置Y坐标，配套补充，避免后续调用报错
    public void setLocationY(int locationY) {
        this.locationY = locationY;
    }


    // 【新增】获取X轴速度，解决子类找不到符号的核心问题
    public int getSpeedX() {
        return speedX;
    }

    // 原有方法保留
    public int getSpeedY() {
        return speedY;
    }

    // 【新增】设置X轴速度，解决子类找不到符号的核心问题
    public void setSpeedX(int speedX) {
        this.speedX = speedX;
    }

    // 【新增】设置Y轴速度，和getSpeedY配套，避免后续子类调用时报错
    public void setSpeedY(int speedY) {
        this.speedY = speedY;
    }

    // 【修正2】setLocation参数类型统一为int，和成员变量匹配，避免强制类型转换
    public void setLocation(int locationX, int locationY) {
        this.locationX = locationX;
        this.locationY = locationY;
    }

    public BufferedImage getImage() {
        if (image == null) {
            image = ImageManager.get(this);
        }
        return image;
    }

    public int getWidth() {
        if (width == -1) {
            // 若未设置，则查询图片宽度并设置
            width = ImageManager.get(this).getWidth();
        }
        return width;
    }

    public int getHeight() {
        if (height == -1) {
            // 若未设置，则查询图片高度并设置
            height = ImageManager.get(this).getHeight();
        }
        return height;
    }

    /**
     * 标记消失
     * isValid = false.
     * notValid() => true.
     */
    public void vanish() {
        isValid = false;
    }

    public boolean notValid() {
        return !this.isValid;
    }

    // 【新增】判断物体是否有效，和子类的射击逻辑配套
    public boolean isValid() {
        return isValid;
    }
}
