package edu.hitsz.aircraft;
import edu.hitsz.application.Main;
import edu.hitsz.strategy.ShootStrategy;

// 必须导入的父类，在basic包
import edu.hitsz.basic.AbstractFlyingObject;
// 必须导入的子弹基类，在bullet包
import edu.hitsz.bullet.BaseBullet;
import edu.hitsz.strategy.SingleStraightStrategy;
// 必须导入的Java集合类
import java.util.List;
// 导入BufferedImage类
import java.awt.image.BufferedImage;
import java.util.List;

/**
 * 所有种类飞机的抽象父类
 * @author hitsz
 */
public abstract class AbstractAircraft extends AbstractFlyingObject implements GameObserver {
    // 标记敌机死亡时是否掉落道具，默认掉落；炸弹道具杀死的敌机不掉落
    protected boolean dropProp = true;

    public boolean isDropProp() {
        return dropProp;
    }

    public void setDropProp(boolean dropProp) {
        this.dropProp = dropProp;
    }

    @Override
    public void onBombEffect() {
        // 炸弹道具生效时的逻辑，后续按表格细化
    }

    @Override
    public void onFreezeEffect() {
        // 冰冻道具生效时的逻辑，后续按表格细化
    }
    // 保存敌机原始速度，用于冰冻后恢复
    protected int originalSpeedX;
    protected int originalSpeedY;
    // 标记敌机是否处于冰冻状态，防止重复触发冰冻
    protected boolean isFrozen = false;

    // 【关键】protected修饰，子类可以继承和修改
    protected BufferedImage image;
    // 最大生命值
    protected int maxHp;
    // 当前生命值
    protected int hp;
    // 新增：射击控制属性，解决子弹连射问题，后面会用到
    protected int shootCounter = 0;
    protected static final int SHOOT_INTERVAL = 40; // 射击间隔，40帧≈0.6秒发射一次
    // 策略模式核心：持有射击策略对象
    protected ShootStrategy shootStrategy;


    /**
     * 构造方法
     */
    public AbstractAircraft(int locationX, int locationY, int speedX, int speedY, int hp) {
        super(locationX, locationY, speedX, speedY);
        this.hp = hp;
        this.maxHp = hp;
    }

    @Override
    public BufferedImage getImage() {
        return this.image;
    }

    // 给子类设置图片的方法
    public void setImage(BufferedImage image) {
        this.image = image;
    }

    // 新增：射击间隔检查方法，统一控制射击频率
    public boolean canShoot() {
        shootCounter++;
        if (shootCounter >= SHOOT_INTERVAL) {
            shootCounter = 0;
            return true;
        }
        return false;
    }

    /**
     * 扣血方法
     */
    public void decreaseHp(int decrease) {
        hp -= decrease;
        if (hp <= 0) {
            hp = 0;
            vanish();
        }
    }

    // ====================== 补全的getter/setter方法，解决HpProp的报错 ======================

    /**
     * 获取当前血量
     */
    public int getHp() {
        return hp;
    }

    /**
     * 获取最大血量（HpProp必须用到的，之前缺少）
     */
    public int getMaxHp() {
        return maxHp;
    }

    /**
     * 设置当前血量（HpProp必须用到的，之前缺少，自带边界保护）
     */
    public void setHp(int hp) {
        // 边界保护：血量最小为0，最大不超过maxHp，避免异常
        this.hp = Math.max(0, Math.min(hp, maxHp));
    }

    /**
     * 飞机射击方法
     * @return 可射击对象需实现，返回子弹列表；非可射击对象空实现，返回空列表
     */
    /**
     * 射击逻辑委托给策略类执行
     *
     * @return 生成的子弹列表
     */
    public List<BaseBullet> shoot() {
        // 委托当前持有的射击策略，执行具体的弹道生成逻辑
        return shootStrategy.shoot(this);
    }

    /**
     * 动态切换射击策略（道具切换弹道用）
     *
     * @param shootStrategy 新的射击策略
     */
    public void setShootStrategy(ShootStrategy shootStrategy) {
        this.shootStrategy = shootStrategy;
    }
    // 重置英雄机状态，解决单例残留问题
    public void reset() {
        // 1. 恢复满血（用你保存的maxHp）
        this.hp = this.maxHp;

        // 2. 恢复初始位置（和Main里的初始化坐标完全一致）
        this.locationX = Main.WINDOW_WIDTH / 2;
        this.locationY = Main.WINDOW_HEIGHT - 100;

        // 3. 用父类提供的方法，强制标记为有效状态（覆盖上一局的死亡状态）
        this.setValid(true);

        // 4. 重置射击策略，防止道具效果残留
        this.shootStrategy = new SingleStraightStrategy(30, -12, true);
    }




}
