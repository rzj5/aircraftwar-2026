package edu.hitsz.aircraftwar.prop;

import edu.hitsz.aircraftwar.aircraft.HeroAircraft;

/**
 * 加血道具：恢复 30 点生命，不超过上限。
 */
public class HpProp extends BaseProp {
    private final int recoverHp = 30;

    public HpProp(int locationX, int locationY, int speedX, int speedY) {
        super(locationX, locationY, speedX, speedY);
        this.image = edu.hitsz.aircraftwar.image.BitmapManager.HP_PROP;
    }

    @Override
    public void active(HeroAircraft heroAircraft) {
        int newHp = Math.min(heroAircraft.getHp() + recoverHp, heroAircraft.getMaxHp());
        heroAircraft.setHp(newHp);
    }
}
