package edu.hitsz.aircraft;
import java.util.ArrayList;
import java.util.List;

public interface GameSubject {
    // 添加观察者（敌机/子弹）
    void addObserver(GameObserver observer);
    // 移除观察者
    void removeObserver(GameObserver observer);
    // 通知所有观察者：炸弹道具生效
    void notifyBomb();
    // 通知所有观察者：冰冻道具生效（先空着，后面实现）
    void notifyFreeze();
}
