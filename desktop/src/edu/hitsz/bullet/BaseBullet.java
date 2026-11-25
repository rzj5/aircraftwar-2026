package edu.hitsz.bullet;

import edu.hitsz.application.Main;
import edu.hitsz.basic.AbstractFlyingObject;
import edu.hitsz.aircraft.GameObserver;

/**
 * 子弹基类
 * @author hitsz
 */
public abstract class BaseBullet extends AbstractFlyingObject implements GameObserver {
        private int power = 0;

        // 👇 【新增】冰冻相关属性，所有子弹共用
        protected int originalSpeedX;
        protected int originalSpeedY;
        protected boolean isFrozen = false;

        // 你原来的构造方法、forward() 等代码都不用改

    public BaseBullet(int locationX, int locationY, int speedX, int speedY, int power) {
        super(locationX, locationY, speedX, speedY);
        this.power = power;
    }

    @Override
    public void forward() {
        // 1. 只做基础移动，不调用父类的super.forward()，屏蔽边界反弹逻辑
        locationX += speedX;
        locationY += speedY;

        // 2. 直接执行你原来的出界销毁逻辑，完全保留你的判断规则
        // 判定 X 轴出界
        if (locationX <= 0 || locationX >= Main.WINDOW_WIDTH) {
            vanish();
        }

        // 判定 Y 轴出界
        if (speedY > 0 && locationY >= Main.WINDOW_HEIGHT) {
            // 向下飞行出界
            vanish();
        } else if (locationY <= 0) {
            // 向上飞行出界
            vanish();
        }
    }


    public int getPower() {
        return power;
    }
    @Override
    public void onBombEffect() {
        // 炸弹道具生效：子弹直接消失
        vanish(); // 用你项目里的vanish()方法，和子弹出界时的逻辑保持一致
    }

    @Override
    public void onFreezeEffect() {
        // 冰冻道具的子弹效果，后续再实现，先空着
    }


}
