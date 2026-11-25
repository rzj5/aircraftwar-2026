package edu.hitsz.prop;

// 必须导入英雄机类，跨包调用必须先导包
import edu.hitsz.aircraft.HeroAircraft;

/**
 * 加血道具：恢复英雄机血量，不超过最大血量上限
 */
public class HpProp extends BaseProp {

    // 单次恢复的血量值，可自行调整
    private final int recoverHp = 30;

    public HpProp(int locationX, int locationY, int speedX, int speedY) {
        super(locationX, locationY, speedX, speedY);
    }

    @Override
    public void active(HeroAircraft heroAircraft) {
        // 计算恢复后的血量，不超过最大血量
        int currentHp = heroAircraft.getHp();
        int maxHp = heroAircraft.getMaxHp();
        int newHp = Math.min(currentHp + recoverHp, maxHp);
        // 设置英雄机血量
        heroAircraft.setHp(newHp);
        System.out.println("加血道具生效！当前血量：" + newHp + "/" + maxHp);
    }

}
