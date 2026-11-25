// 1. 包声明：必须和你prop文件夹的路径完全一致，不能修改
package edu.hitsz.prop;

// 2. 导入父类AbstractFlyingObject（二选一，根据你实际的文件位置选）
// 情况1：如果AbstractFlyingObject在basic文件夹里，保留下面这行（90%的模板都是这个结构）
import edu.hitsz.basic.AbstractFlyingObject;
// 情况2：如果AbstractFlyingObject在aircraft文件夹里，注释上面一行，取消下面这行的注释
// import edu.hitsz.aircraft.AbstractFlyingObject;

// 3. 导入英雄机HeroAircraft（固定在aircraft文件夹，不用改）
import edu.hitsz.aircraft.HeroAircraft;

/**
 * 道具抽象父类，所有具体道具的基类
 */
public abstract class BaseProp extends AbstractFlyingObject {

    // 道具生效时长（后续扩展道具用，可保留）
    protected int activeTime;

    /**
     * 构造方法，调用父类完成基础属性初始化
     * @param locationX 道具初始X坐标
     * @param locationY 道具初始Y坐标
     * @param speedX 道具水平移动速度
     * @param speedY 道具垂直移动速度
     */
    public BaseProp(int locationX, int locationY, int speedX, int speedY) {
        super(locationX, locationY, speedX, speedY);
    }

    /**
     * 抽象方法：道具生效逻辑
     * 所有具体道具（加血、火力道具等）必须实现这个方法
     * @param heroAircraft 英雄机实例，道具作用的目标
     */
    public abstract void active(HeroAircraft heroAircraft);

}
