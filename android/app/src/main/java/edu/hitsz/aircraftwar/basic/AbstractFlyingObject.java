package edu.hitsz.aircraftwar.basic;

import android.graphics.Bitmap;

import edu.hitsz.aircraftwar.aircraft.AbstractAircraft;
import edu.hitsz.aircraftwar.util.GameConfig;

/**
 * 所有飞行物体的基类（飞机、子弹、道具）
 * 与桌面版 edu.hitsz.basic.AbstractFlyingObject 逻辑一致，仅去掉 AWT/Swing 依赖。
 */
public abstract class AbstractFlyingObject {
    protected int locationX;
    protected int locationY;
    protected int speedX;
    protected int speedY;
    protected Bitmap image = null;
    protected int width = -1;
    protected int height = -1;
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

    public void forward() {
        locationX += speedX;
        locationY += speedY;

        int leftEdge = locationX - width / 2;
        int rightEdge = locationX + width / 2;
        if (leftEdge <= 0) {
            locationX = width / 2;
            speedX = -speedX;
        }
        if (rightEdge >= GameConfig.VIRTUAL_WIDTH) {
            locationX = GameConfig.VIRTUAL_WIDTH - width / 2;
            speedX = -speedX;
        }
    }

    public boolean crash(AbstractFlyingObject flyingObject) {
        int factor = this instanceof AbstractAircraft ? 2 : 1;
        int fFactor = flyingObject instanceof AbstractAircraft ? 2 : 1;

        int x = flyingObject.getLocationX();
        int y = flyingObject.getLocationY();
        int fWidth = flyingObject.getWidth();
        int fHeight = flyingObject.getHeight();

        return x + (fWidth + this.getWidth()) / 2 > locationX
                && x - (fWidth + this.getWidth()) / 2 < locationX
                && y + (fHeight / fFactor + this.getHeight() / factor) / 2 > locationY
                && y - (fHeight / fFactor + this.getHeight() / factor) / 2 < locationY;
    }

    public int getLocationX() {
        return locationX;
    }

    public int getLocationY() {
        return locationY;
    }

    public void setLocationX(int locationX) {
        this.locationX = locationX;
    }

    public void setLocationY(int locationY) {
        this.locationY = locationY;
    }

    public int getSpeedX() {
        return speedX;
    }

    public int getSpeedY() {
        return speedY;
    }

    public void setSpeedX(int speedX) {
        this.speedX = speedX;
    }

    public void setSpeedY(int speedY) {
        this.speedY = speedY;
    }

    public void setLocation(int locationX, int locationY) {
        this.locationX = locationX;
        this.locationY = locationY;
    }

    public Bitmap getImage() {
        return image;
    }

    public int getWidth() {
        if (width == -1) {
            width = image == null ? 0 : image.getWidth();
        }
        return width;
    }

    public int getHeight() {
        if (height == -1) {
            height = image == null ? 0 : image.getHeight();
        }
        return height;
    }

    public void vanish() {
        isValid = false;
    }

    public boolean notValid() {
        return !this.isValid;
    }

    public boolean isValid() {
        return isValid;
    }
}
