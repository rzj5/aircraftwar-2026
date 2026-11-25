package edu.hitsz.aircraftwar.strategy;

import edu.hitsz.aircraftwar.aircraft.AbstractAircraft;
import edu.hitsz.aircraftwar.bullet.BaseBullet;

import java.util.List;

public interface ShootStrategy {
    List<BaseBullet> shoot(AbstractAircraft aircraft);
}
