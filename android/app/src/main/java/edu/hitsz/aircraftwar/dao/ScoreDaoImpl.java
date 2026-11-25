package edu.hitsz.aircraftwar.dao;

import android.content.Context;
import android.util.Log;

import edu.hitsz.aircraftwar.entity.ScoreRecord;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 文件存储版 DAO：与桌面版 ScoreDaoImpl 逻辑一致，只是把文件放在应用私有目录
 * (context.getFilesDir())，不再依赖工作目录相对路径。
 * 每个难度一个 txt：easy_score.txt / normal_score.txt / hard_score.txt。
 */
public class ScoreDaoImpl implements ScoreDao {
    private static final String TAG = "ScoreDaoImpl";
    private final String difficulty;
    private final File file;

    public ScoreDaoImpl(Context context, String difficulty) {
        this.difficulty = difficulty;
        this.file = new File(context.getApplicationContext().getFilesDir(), difficulty + "_score.txt");
        initFile();
    }

    private void initFile() {
        if (!file.exists()) {
            try {
                File parent = file.getParentFile();
                if (parent != null && !parent.exists()) {
                    parent.mkdirs();
                }
                file.createNewFile();
            } catch (IOException e) {
                Log.e(TAG, "创建成绩文件失败: " + file.getAbsolutePath(), e);
            }
        }
    }

    @Override
    public List<ScoreRecord> getAllRecords() {
        List<ScoreRecord> records = new ArrayList<>();
        if (!file.exists()) {
            return records;
        }
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] fields = line.split(",");
                if (fields.length == 4) {
                    try {
                        records.add(new ScoreRecord(fields[0], Integer.parseInt(fields[1]),
                                ScoreRecord.parseTime(fields[2]), fields[3]));
                    } catch (Exception ignored) {
                        // 单行格式异常时跳过，不影响其它记录
                    }
                }
            }
        } catch (IOException e) {
            Log.e(TAG, "读取成绩文件失败", e);
        }
        return records.stream()
                .sorted(Comparator.comparingInt(ScoreRecord::getScore).reversed())
                .collect(Collectors.toList());
    }

    @Override
    public void addRecord(ScoreRecord record) {
        String line = String.format("%s,%d,%s,%s",
                record.getUserName(), record.getScore(), record.getFormattedTime(), record.getDifficulty());
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(file, true))) {
            bw.write(line);
            bw.newLine();
        } catch (IOException e) {
            Log.e(TAG, "写入成绩失败", e);
        }
    }

    @Override
    public void deleteRecord(int rank) {
        List<ScoreRecord> all = getAllRecords();
        if (rank < 1 || rank > all.size()) {
            Log.w(TAG, "要删除的名次不存在: " + rank);
            return;
        }
        all.remove(rank - 1);
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(file))) {
            for (ScoreRecord record : all) {
                bw.write(String.format("%s,%d,%s,%s",
                        record.getUserName(), record.getScore(), record.getFormattedTime(), record.getDifficulty()));
                bw.newLine();
            }
        } catch (IOException e) {
            Log.e(TAG, "删除成绩后重写失败", e);
        }
    }

    @Override
    public void printRankingList() {
        List<ScoreRecord> records = getAllRecords();
        Log.i(TAG, "===== 得分排行榜(" + difficulty + ") =====");
        if (records.isEmpty()) {
            Log.i(TAG, "暂无游戏记录");
            return;
        }
        for (int i = 0; i < records.size(); i++) {
            Log.i(TAG, "第" + (i + 1) + "名：" + records.get(i));
        }
    }
}
