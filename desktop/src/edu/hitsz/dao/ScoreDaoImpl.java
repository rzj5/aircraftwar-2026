package edu.hitsz.dao;

import edu.hitsz.entity.ScoreRecord;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * DAO接口实现类，对应指导书的BookDaoImpl
 * 实现文件读写、数据操作，不同难度对应单独的存储文件
 */
public class ScoreDaoImpl implements ScoreDao {
    // 时间格式化工具，和实体类保持一致
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    // 当前操作的游戏难度
    private final String difficulty;
    // 存储文件的路径（不同难度对应不同文件）
    private final String filePath;

    // 构造方法：传入游戏难度，自动对应存储文件
    public ScoreDaoImpl(String difficulty) {
        this.difficulty = difficulty;
        // 不同难度对应不同的txt文件，满足实验要求
        this.filePath = difficulty + "_score.txt";
        // 初始化：如果文件不存在，自动创建
        initFile();
    }

    // 初始化文件，不存在则创建
    private void initFile() {
        File file = new File(filePath);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 实现：查询所有记录，按分数降序排序
     */
    @Override
    public List<ScoreRecord> getAllRecords() {
        List<ScoreRecord> records = new ArrayList<>();
        // 从文件里读取所有记录
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                // 按分隔符拆分字段，格式：玩家名,分数,时间,难度
                String[] fields = line.split(",");
                if (fields.length == 4) {
                    ScoreRecord record = new ScoreRecord(
                            fields[0],
                            Integer.parseInt(fields[1]),
                            LocalDateTime.parse(fields[2], FORMATTER),
                            fields[3]
                    );
                    records.add(record);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // 按分数降序排序，满足排行榜要求
        return records.stream()
                .sorted(Comparator.comparingInt(ScoreRecord::getScore).reversed())
                .collect(Collectors.toList());
    }

    /**
     * 实现：新增一条记录到文件
     */
    @Override
    public void addRecord(ScoreRecord record) {
        // 把记录转成一行文本，用逗号分隔
        String line = String.format("%s,%d,%s,%s",
                record.getUserName(),
                record.getScore(),
                record.getFormattedTime(),
                record.getDifficulty()
        );

        // 追加写入文件
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filePath, true))) {
            bw.write(line);
            bw.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 实现：按名次删除指定记录
     */
    @Override
    public void deleteRecord(int rank) {
        List<ScoreRecord> allRecords = getAllRecords();
        // 校验名次是否合法
        if (rank < 1 || rank > allRecords.size()) {
            System.out.println("要删除的名次不存在！");
            return;
        }

        // 移除指定名次的记录（名次从1开始，集合索引从0开始）
        allRecords.remove(rank - 1);

        // 把剩下的记录重新写入文件
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filePath))) {
            for (ScoreRecord record : allRecords) {
                String line = String.format("%s,%d,%s,%s",
                        record.getUserName(),
                        record.getScore(),
                        record.getFormattedTime(),
                        record.getDifficulty()
                );
                bw.write(line);
                bw.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("已删除第" + rank + "名的记录！");
    }

    /**
     * 控制台打印排行榜，满足实验要求
     */
    public void printRankingList() {
        List<ScoreRecord> records = getAllRecords();
        System.out.println("\n==================== 得分排行榜 ====================");
        System.out.println("当前难度：" + difficulty);
        if (records.isEmpty()) {
            System.out.println("暂无游戏记录！");
            System.out.println("====================================================");
            return;
        }

        // 按名次打印
        for (int i = 0; i < records.size(); i++) {
            System.out.printf("第%d名：%s%n", i + 1, records.get(i));
        }
        System.out.println("====================================================\n");
    }
}