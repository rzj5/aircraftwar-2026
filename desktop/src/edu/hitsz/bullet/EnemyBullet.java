package edu.hitsz.bullet;

/**
 * 敌机子弹
 * @Author hitsz
 */
public class EnemyBullet extends BaseBullet {
    public EnemyBullet(int locationX, int locationY, int speedX, int speedY, int power) {
        super(locationX, locationY, speedX, speedY, power);
    }

    // 👇 【新增】实现冰冻效果
    @Override
    public void onFreezeEffect() {
        // 如果已经处于冰冻状态，直接返回，防止重复触发
        if (isFrozen) {
            return;
        }
        isFrozen = true;

        // 1. 保存原始速度
        originalSpeedX = this.speedX;
        originalSpeedY = this.speedY;
        // 2. 设为静止
        this.speedX = 0;
        this.speedY = 0;

        // 3. 开线程，5秒后恢复速度（不阻塞游戏主线程）
        new Thread(() -> {
            try {
                Thread.sleep(5000); // 静止5秒
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            // 恢复原始速度
            this.speedX = originalSpeedX;
            this.speedY = originalSpeedY;
            isFrozen = false;
        }).start();
    }
}
