package edu.hitsz.aircraftwar.prop;

import edu.hitsz.aircraftwar.aircraft.GameObserver;
import edu.hitsz.aircraftwar.aircraft.GameSubject;
import edu.hitsz.aircraftwar.aircraft.HeroAircraft;

import java.util.ArrayList;
import java.util.List;

/**
 * 冰冻道具：敌机与敌方子弹静止若干秒。
 */
public class FreezeProp extends BaseProp implements GameSubject {
    private final List<GameObserver> observers = new ArrayList<>();

    public FreezeProp(int locationX, int locationY, int speedX, int speedY) {
        super(locationX, locationY, speedX, speedY);
        this.image = edu.hitsz.aircraftwar.image.BitmapManager.FREEZE_PROP;
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
    public void notifyFreeze() {
        for (GameObserver o : observers) {
            o.onFreezeEffect();
        }
        observers.clear();
    }

    @Override
    public void notifyBomb() {
    }

    @Override
    public void active(HeroAircraft heroAircraft) {
    }
}
