package edu.hitsz.aircraft;

import edu.hitsz.application.ImageManager;
import edu.hitsz.application.Main;
import edu.hitsz.bullet.BaseBullet;

import java.util.LinkedList;
import java.util.List;

/**
 * 普通敌机
 * 不可射击、不掉落道具
 * @author hitsz
 */
public class MobEnemy extends AbstractAircraft {

    public MobEnemy(int locationX, int locationY, int speedX, int speedY, int hp) {
        super(locationX, locationY, speedX, speedY, hp);
    }

    @Override
    public void forward() {
        super.forward();
        // 绑定普通敌机的图片，解决空指针
        this.image = ImageManager.MOB_ENEMY_IMAGE;
        // 判定 y 轴向下飞行出界
        if (locationY >= Main.WINDOW_HEIGHT ) {
            vanish();
        }
    }
    @Override
    public void onFreezeEffect() {
        if (isFrozen) return; // 防止重复冰冻
        isFrozen = true;
        // 保存原始速度（其实普通敌机不用恢复，但统一写）
        originalSpeedX = this.speedX;
        originalSpeedY = this.speedY;
        // 永久静止，速度设为0
        this.speedX = 0;
        this.speedY = 0;
    }

    @Override
    public List<BaseBullet> shoot() {
        return new LinkedList<>();
    }

}
