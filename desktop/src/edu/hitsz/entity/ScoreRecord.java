package edu.hitsz.entity;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 分数记录实体类，对应指导书的Book类
 * 封装玩家的游戏记录：玩家名、分数、游戏时间、难度
 */
public class ScoreRecord {
    // 玩家名（实验要求用默认名即可）
    private String userName;
    // 游戏得分
    private int score;
    // 游戏结束时间
    private LocalDateTime gameTime;
    // 游戏难度（easy/normal/hard，对应不同的存储文件）
    private String difficulty;

    // 时间格式化工具
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    // 构造方法
    public ScoreRecord(String userName, int score, LocalDateTime gameTime, String difficulty) {
        this.userName = userName;
        this.score = score;
        this.gameTime = gameTime;
        this.difficulty = difficulty;
    }

    // 所有属性的getter和setter
    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public LocalDateTime getGameTime() {
        return gameTime;
    }

    public void setGameTime(LocalDateTime gameTime) {
        this.gameTime = gameTime;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }

    // 把时间转成字符串，方便文件存储和打印
    public String getFormattedTime() {
        return gameTime.format(FORMATTER);
    }

    // 重写toString，方便控制台打印排行榜
    @Override
    public String toString() {
        return String.format("玩家：%s | 得分：%d | 时间：%s", userName, score, getFormattedTime());
    }
}
