package edu.hitsz.aircraftwar.dao;

import edu.hitsz.aircraftwar.entity.ScoreRecord;

import java.util.List;

public interface ScoreDao {
    List<ScoreRecord> getAllRecords();

    void addRecord(ScoreRecord record);

    void deleteRecord(int rank);

    void printRankingList();
}
