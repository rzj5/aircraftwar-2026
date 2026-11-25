package edu.hitsz.aircraft;

import edu.hitsz.application.ImageManager;
import edu.hitsz.application.Main;
import edu.hitsz.strategy.RingShootStrategy;

/**
 * 超级Boss敌机
 * 策略模式：委托RingShootStrategy实现20颗环射弹道
 */
public class SuperBoss extends AbstractAircraft {

    public SuperBoss(int locationX, int locationY, int speedX, int speedY, int hp) {
        super(locationX, locationY, speedX, speedY, hp);
        // 绑定专属图片
        this.image = ImageManager.SUPER_BOSS_IMAGE;
        // 初始化射击策略：20颗环射，伤害20，子弹速度8，敌机子弹
        this.shootStrategy = new RingShootStrategy(20, 8, false, 20);
    }
    @Override
    public void onFreezeEffect() {
        // Boss不受冰冻道具影响，什么都不做
    }

    @Override
    public void forward() {
        // 【实验要求】仅左右移动，不向下移动，固定在屏幕顶部
        // 直接修改X坐标，实现水平移动
        setLocationX(getLocationX() + getSpeedX());
        // 左右撞墙反弹
        if (getLocationX() <= 0 || getLocationX() + getWidth() >= Main.WINDOW_WIDTH) {
            setSpeedX(-getSpeedX());
        }
    }
}
