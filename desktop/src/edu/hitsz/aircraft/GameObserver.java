package edu.hitsz.aircraft;

public interface GameObserver {
    // 炸弹道具生效时的回调
    void onBombEffect();
    // 冰冻道具生效时的回调
    void onFreezeEffect();
}
