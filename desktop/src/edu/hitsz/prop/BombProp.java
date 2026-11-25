package edu.hitsz.prop;

import edu.hitsz.aircraft.GameObserver;
import edu.hitsz.aircraft.GameSubject;
import edu.hitsz.aircraft.HeroAircraft;
import edu.hitsz.application.AudioManager;
import java.util.ArrayList;
import java.util.List;

// 和 FreezeProp 一样，实现 GameSubject 接口
public class BombProp extends BaseProp implements GameSubject {
    // 和 FreezeProp 一样，定义观察者列表
    private final List<GameObserver> observers = new ArrayList<>();

    public BombProp(int locationX, int locationY, int speedX, int speedY) {
        super(locationX, locationY, speedX, speedY);
    }

    // 1. 实现 addObserver 方法（和 FreezeProp 完全一样）
    @Override
    public void addObserver(GameObserver observer) {
        observers.add(observer);
    }

    // 2. 实现 removeObserver 方法（和 FreezeProp 完全一样）
    @Override
    public void removeObserver(GameObserver observer) {
        observers.remove(observer);
    }

    // 3. 实现 notifyBomb 方法（和 FreezeProp 的 notifyFreeze 对应）
    @Override
    public void notifyBomb() {
        // 遍历所有观察者，触发炸弹效果
        for (GameObserver observer : observers) {
            observer.onBombEffect();
        }
        // 清空观察者，避免重复触发
        observers.clear();
    }

    // 4. 实现 notifyFreeze 方法（炸弹道具不需要，空实现即可）
    @Override
    public void notifyFreeze() {
        // 空实现，和 FreezeProp 保持接口兼容
    }

    // 5. 实现父类 BaseProp 的抽象方法 active（和其他道具一致）
    @Override
    public void active(HeroAircraft heroAircraft) {
        // 这里只播放音效，清屏逻辑完全交给观察者模式实现
        AudioManager.getInstance().playSoundEffect(AudioManager.BOMB_EXPLOSION);
    }
}
