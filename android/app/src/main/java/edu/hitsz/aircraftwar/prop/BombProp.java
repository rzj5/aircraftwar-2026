package edu.hitsz.aircraftwar.prop;

import edu.hitsz.aircraftwar.aircraft.GameObserver;
import edu.hitsz.aircraftwar.aircraft.GameSubject;
import edu.hitsz.aircraftwar.aircraft.HeroAircraft;

import java.util.ArrayList;
import java.util.List;

/**
 * 炸弹道具：观察者模式清屏（敌方子弹与敌机一起消失）。
 */
public class BombProp extends BaseProp implements GameSubject {
    private final List<GameObserver> observers = new ArrayList<>();

    public BombProp(int locationX, int locationY, int speedX, int speedY) {
        super(locationX, locationY, speedX, speedY);
        this.image = edu.hitsz.aircraftwar.image.BitmapManager.BOMB_PROP;
    }

    @Override
    public void addObserver(GameObserver observer) {
        observers.add(observer);
    }

    @Override
    public void removeObserver(GameObserver observer) {
        observers.remove(observer);
    }

    @Override
    public void notifyBomb() {
        for (GameObserver observer : observers) {
            observer.onBombEffect();
        }
        observers.clear();
    }

    @Override
    public void notifyFreeze() {
    }

    @Override
    public void active(HeroAircraft heroAircraft) {
        // 音效由 BaseGame 播放，清屏逻辑交给观察者
    }
}
