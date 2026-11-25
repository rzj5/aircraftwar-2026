package edu.hitsz.aircraft;

import edu.hitsz.application.ImageManager;
import edu.hitsz.application.Main;
import edu.hitsz.strategy.ScatterStrategy;

/**
 * 王牌敌机
 * 策略模式：委托ScatterStrategy实现3发散射弹道
 */
public class BossEnemy extends AbstractAircraft {

    public BossEnemy(int locationX, int locationY, int speedX, int speedY, int hp) {
        super(locationX, locationY, speedX, speedY, hp);
        // 绑定专属图片
        this.image = ImageManager.BOSS_ENEMY_IMAGE;
        // 初始化射击策略：3发散射，伤害15，子弹速度10向下，敌机子弹
        this.shootStrategy = new ScatterStrategy(15, 10, false, 3);
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

    @Override
    public void forward() {
        // 垂直下落+左右移动，撞墙反弹
        super.forward();
        if (getLocationX() <= 0 || getLocationX() + getWidth() >= Main.WINDOW_WIDTH) {
            setSpeedX(-getSpeedX());
        }
    }

}
