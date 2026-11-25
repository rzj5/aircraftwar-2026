package edu.hitsz.prop;

import edu.hitsz.aircraft.GameObserver;
import edu.hitsz.aircraft.GameSubject;
import edu.hitsz.aircraft.HeroAircraft;
import edu.hitsz.application.AudioManager;
import java.util.ArrayList;
import java.util.List;

public class FreezeProp extends BaseProp implements GameSubject {
    private List<GameObserver> observers = new ArrayList<>();

    public FreezeProp(int locationX, int locationY, int speedX, int speedY) {
        super(locationX, locationY, speedX, speedY);
    }

    @Override
    public void addObserver(GameObserver observer) {
        observers.add(observer);
    }

    @Override
    public void removeObserver(GameObserver observer) {
        observers.remove(observer);
    }

    // 冰冻正常通知
    @Override
    public void notifyFreeze() {
        for (GameObserver o : observers) {
            o.onFreezeEffect(); // 调用子类自己的冰冻效果
        }
        observers.clear();
    }

    // 只补这一行：炸弹通知空实现，只为接口兼容，不影响冰冻
    @Override
    public void notifyBomb() {}

    @Override
    public void active(HeroAircraft heroAircraft) {
    }
}
