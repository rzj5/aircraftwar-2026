package edu.hitsz.aircraft;

import edu.hitsz.application.ImageManager;
import edu.hitsz.application.Main;
import edu.hitsz.strategy.DoubleStraightStrategy;

/**
 * 精锐敌机
 * 策略模式：委托DoubleStraightStrategy实现双排直射弹道
 */
public class ElitePlusEnemy extends AbstractAircraft {

    public ElitePlusEnemy(int locationX, int locationY, int speedX, int speedY, int hp) {
        super(locationX, locationY, speedX, speedY, hp);
        // 绑定专属图片
        this.image = ImageManager.ELITE_PLUS_ENEMY_IMAGE;
        // 初始化射击策略：双排直射，伤害10，子弹速度12向下
        this.shootStrategy = new DoubleStraightStrategy(10, 12);
    }

    @Override
    public void forward() {
        // 垂直下落+左右移动，撞墙反弹
        super.forward();
        if (getLocationX() <= 0 || getLocationX() + getWidth() >= Main.WINDOW_WIDTH) {
            setSpeedX(-getSpeedX());
        }
    }
    @Override
    public void onFreezeEffect() {
        if (isFrozen) return;
        isFrozen = true;
        originalSpeedX = this.speedX;
        originalSpeedY = this.speedY;
        this.speedX = 0;
        this.speedY = 0;

        new Thread(() -> {
            try {
                Thread.sleep(3000); // 静止3秒
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            this.speedX = originalSpeedX;
            this.speedY = originalSpeedY;
            isFrozen = false;
        }).start();
    }

}

