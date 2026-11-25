package edu.hitsz.aircraftwar.entity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * 分数记录实体：玩家名、分数、游戏时间、难度。
 * 与桌面版 edu.hitsz.entity.ScoreRecord 字段一致，时间用 Date + SimpleDateFormat
 * 替代 LocalDateTime（避免 API 级别/desugar 依赖）。
 */
public class ScoreRecord {
    private String userName;
    private int score;
    private Date gameTime;
    private String difficulty;

    private static final String PATTERN = "yyyy-MM-dd HH:mm:ss";

    public ScoreRecord(String userName, int score, Date gameTime, String difficulty) {
        this.userName = userName;
        this.score = score;
        this.gameTime = gameTime;
        this.difficulty = difficulty;
    }

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

    public Date getGameTime() {
        return gameTime;
    }

    public void setGameTime(Date gameTime) {
        this.gameTime = gameTime;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }

    public String getFormattedTime() {
        return new SimpleDateFormat(PATTERN, Locale.CHINA).format(gameTime);
    }

    public static Date parseTime(String text) throws Exception {
        return new SimpleDateFormat(PATTERN, Locale.CHINA).parse(text);
    }

    @Override
    public String toString() {
        return String.format(Locale.CHINA, "玩家：%s | 得分：%d | 时间：%s", userName, score, getFormattedTime());
    }
}
