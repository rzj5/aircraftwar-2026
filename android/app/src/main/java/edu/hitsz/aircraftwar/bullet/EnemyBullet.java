package edu.hitsz.aircraftwar.bullet;

/**
 * 敌机子弹：冰冻时静止 5 秒后恢复。
 * 桌面版用 new Thread(sleep) 实现；Android 侧同样用线程，但恢复动作通过对象自身的速度字段完成。
 */
public class EnemyBullet extends BaseBullet {

    public EnemyBullet(int locationX, int locationY, int speedX, int speedY, int power) {
        super(locationX, locationY, speedX, speedY, power);
    }

    @Override
    public void onFreezeEffect() {
        if (isFrozen) {
            return;
        }
        isFrozen = true;
        originalSpeedX = this.speedX;
        originalSpeedY = this.speedY;
        this.speedX = 0;
        this.speedY = 0;

        new Thread(() -> {
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            this.speedX = originalSpeedX;
            this.speedY = originalSpeedY;
            isFrozen = false;
        }, "enemy-bullet-freeze").start();
    }
}
