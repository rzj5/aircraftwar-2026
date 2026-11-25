package edu.hitsz.aircraftwar.aircraft;

public interface GameSubject {
    void addObserver(GameObserver observer);

    void removeObserver(GameObserver observer);

    void notifyBomb();

    void notifyFreeze();
}
