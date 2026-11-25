package edu.hitsz.prop;

import java.util.Random;

/**
 * 道具简单工厂，完全兼容你的项目
 * 同时支持静态调用、按敌机类型分掉落池生成
 */
public class PropSimpleFactory {
    // 【关键修改】Random改为静态，供静态方法使用
    private static final Random random = new Random();
    // 道具统一掉落速度，可自行调整
    private static final int PROP_FALL_SPEED = 3;

    // ========== 道具类型枚举 ==========
    private enum PropType {
        HP,         // 加血道具（对应你的HpProp）
        FIRE,       // 火力道具（散射）
        FIRE_PLUS,  // 超级火力道具（环射）
        BOMB,       // 炸弹道具
        FREEZE      // 冰冻道具
    }

    // ==============================================
    // 【核心修复】方法改为static静态，解决静态调用报错
    // ==============================================
    /**
     * 兼容旧逻辑：通过propType字符串生成道具，支持静态调用
     * @param propType 道具类型字符串，对应Game里的"hp"/"fire"/"firePlus"/"bomb"/"freeze"
     * @param locationX 道具X坐标
     * @param locationY 道具Y坐标
     * @param speedX 道具X速度
     * @param speedY 道具Y速度
     * @return 对应道具实例
     */
    public static BaseProp createProp(String propType, int locationX, int locationY, int speedX, int speedY) {
        // 完全匹配你Game里的propType字符串，不区分大小写
        switch (propType.toLowerCase()) {
            case "hp":
            case "blood":
                return new HpProp(locationX, locationY, speedX, speedY);
            case "fire":
                return new FireSupplyProp(locationX, locationY, speedX, speedY);
            case "fireplus":
            case "fire_plus":
            case "firePlus":
                return new FirePlusSupplyProp(locationX, locationY, speedX, speedY);
            case "bomb":
                return new BombProp(locationX, locationY, speedX, speedY);
            case "freeze":
                return new FreezeProp(locationX, locationY, speedX, speedY);
            default:
                return null;
        }
    }

    // ==============================================
    // 按敌机类型分掉落池的专用方法（之前的需求保留）
    // ==============================================
    /**
     * 【精英敌机专用】掉落3种道具：加血、火力、超级火力
     */
    public BaseProp createEliteProp(int locationX, int locationY) {
        PropType[] allowProps = {PropType.HP, PropType.FIRE, PropType.FIRE_PLUS};
        PropType randomType = allowProps[random.nextInt(allowProps.length)];
        return createPropByType(randomType, locationX, locationY);
    }

    /**
     * 【精锐敌机专用】生成4种道具（不含冰冻）
     */
    public BaseProp createElitePlusProp(int locationX, int locationY) {
        PropType[] allowProps = {PropType.HP, PropType.FIRE, PropType.FIRE_PLUS, PropType.BOMB};
        PropType randomType = allowProps[random.nextInt(allowProps.length)];
        return createPropByType(randomType, locationX, locationY);
    }

    /**
     * 【王牌敌机专用】生成全部5种道具
     */
    public BaseProp createBossProp(int locationX, int locationY) {
        PropType[] allowProps = PropType.values();
        PropType randomType = allowProps[random.nextInt(allowProps.length)];
        return createPropByType(randomType, locationX, locationY);
    }

    /**
     * 【超级Boss专用】全道具池
     */
    public BaseProp createSuperBossProp(int locationX, int locationY) {
        PropType[] allowProps = PropType.values();
        PropType randomType = allowProps[random.nextInt(allowProps.length)];
        return createPropByType(randomType, locationX, locationY);
    }

    /**
     * 内部方法：根据枚举类型生成道具实例
     */
    private BaseProp createPropByType(PropType type, int locationX, int locationY) {
        switch (type) {
            case HP:
                return new HpProp(locationX, locationY, 0, PROP_FALL_SPEED);
            case FIRE:
                return new FireSupplyProp(locationX, locationY, 0, PROP_FALL_SPEED);
            case FIRE_PLUS:
                return new FirePlusSupplyProp(locationX, locationY, 0, PROP_FALL_SPEED);
            case BOMB:
                return new BombProp(locationX, locationY, 0, PROP_FALL_SPEED);
            case FREEZE:
                return new FreezeProp(locationX, locationY, 0, PROP_FALL_SPEED);
            default:
                return null;
        }
    }
}
