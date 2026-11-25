package edu.hitsz.aircraftwar.prop;

/**
 * 道具简单工厂：与桌面版 edu.hitsz.prop.PropSimpleFactory 一一对应。
 */
public class PropSimpleFactory {
    private static final int PROP_FALL_SPEED = 3;

    public static BaseProp createProp(String propType, int locationX, int locationY, int speedX, int speedY) {
        if (propType == null) {
            return null;
        }
        switch (propType.toLowerCase()) {
            case "hp":
            case "blood":
                return new HpProp(locationX, locationY, speedX, speedY);
            case "fire":
                return new FireSupplyProp(locationX, locationY, speedX, speedY);
            case "fireplus":
            case "fire_plus":
                return new FirePlusSupplyProp(locationX, locationY, speedX, speedY);
            case "bomb":
                return new BombProp(locationX, locationY, speedX, speedY);
            case "freeze":
                return new FreezeProp(locationX, locationY, speedX, speedY);
            default:
                return null;
        }
    }

    public static int fallSpeed() {
        return PROP_FALL_SPEED;
    }
}
