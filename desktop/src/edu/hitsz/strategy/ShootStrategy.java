package edu.hitsz.strategy;

import edu.hitsz.aircraft.AbstractAircraft;
import edu.hitsz.bullet.BaseBullet;
import java.util.List;

/**
 * 射击策略接口（抽象策略角色）
 * 定义所有射击策略的统一规范
 */
public interface ShootStrategy {
    /**
     * 执行射击逻辑，生成子弹
     * @param aircraft 射击的飞机对象（获取位置、宽度等属性）
     * @return 生成的子弹列表
     */
    List<BaseBullet> shoot(AbstractAircraft aircraft);
}
