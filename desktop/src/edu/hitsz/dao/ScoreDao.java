package edu.hitsz.dao;

import edu.hitsz.entity.ScoreRecord;
import java.util.List;

/**
 * 分数数据访问接口，对应指导书的BookDao接口
 * 定义排行榜数据的增、删、查操作标准
 */
public interface ScoreDao {
    /**
     * 查询当前难度下的所有分数记录（按分数降序排序）
     * @return 排序后的分数记录列表
     */
    List<ScoreRecord> getAllRecords();

    /**
     * 新增一条分数记录
     * @param record 要新增的分数记录
     */
    void addRecord(ScoreRecord record);

    /**
     * 按序号删除指定的分数记录
     * @param rank 要删除的记录名次（排行榜里的序号）
     */
    void deleteRecord(int rank);

    /**
     * 控制台打印排行榜，满足实验要求
     */
    void printRankingList();
}
