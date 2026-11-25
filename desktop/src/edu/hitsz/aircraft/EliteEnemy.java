package edu.hitsz.aircraft;

import edu.hitsz.application.ImageManager;
import edu.hitsz.application.Main;
import edu.hitsz.strategy.SingleStraightStrategy;

/**
 * 精英敌机类
 */
public class EliteEnemy extends AbstractAircraft {

    public EliteEnemy(int locationX, int locationY, int speedX, int speedY, int hp) {
        super(locationX, locationY, speedX, speedY, hp);
        // 绑定图片
        this.image = ImageManager.ELITE_ENEMY_IMAGE;
        // 初始化射击策略
        this.shootStrategy = new SingleStraightStrategy(10, 12, false);
    }
    @Override
    public void onFreezeEffect() {
        if (isFrozen) return;
        isFrozen = true;
        // 保存原始速度
        originalSpeedX = this.speedX;
        originalSpeedY = this.speedY;
        // 设为静止
        this.speedX = 0;
        this.speedY = 0;

        // 开线程延时恢复，不阻塞主线程
        new Thread(() -> {
            try {
                Thread.sleep(4000); // 静止4秒
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            // 恢复速度
            this.speedX = originalSpeedX;
            this.speedY = originalSpeedY;
            isFrozen = false;
        }).start();
    }

    @Override
    public void forward() {
        // 敌机移动逻辑：垂直向下+水平小幅移动
        super.forward();
        // 左右撞墙反弹
        if (getLocationX() <= 0 || getLocationX() + getWidth() >= Main.WINDOW_WIDTH) {
            setSpeedX(-getSpeedX());
        }
    }
}
