package edu.hitsz.strategy;

import edu.hitsz.aircraft.AbstractAircraft;
import edu.hitsz.bullet.BaseBullet;
import java.util.ArrayList;
import java.util.List;

/**
 * 不射击策略
 * 对应普通敌机
 */
public class NoShootStrategy implements ShootStrategy {
    @Override
    public List<BaseBullet> shoot(AbstractAircraft aircraft) {
        // 空列表，不生成子弹
        return new ArrayList<>();
    }
}
