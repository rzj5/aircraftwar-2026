package edu.hitsz.aircraftwar;

import android.graphics.Color;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

import edu.hitsz.aircraftwar.dao.ScoreDao;
import edu.hitsz.aircraftwar.dao.ScoreDaoImpl;
import edu.hitsz.aircraftwar.entity.ScoreRecord;

/**
 * 排行榜页面（实验4 预留，本实验先给出可用的实现）。
 * 对应桌面版 edu.hitsz.ui.RankPanel，数据源同为按难度分文件的 ScoreDao。
 */
public class RankActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rank);

        String difficulty = getIntent().getStringExtra(MainActivity.EXTRA_DIFFICULTY);
        if (difficulty == null) {
            difficulty = MainActivity.DIFFICULTY_NORMAL;
        }

        TextView title = findViewById(R.id.rank_title);
        title.setText(difficulty + " 难度 得分排行榜");

        LinearLayout list = findViewById(R.id.rank_list);
        ScoreDao dao = new ScoreDaoImpl(this, difficulty);
        List<ScoreRecord> records = dao.getAllRecords();

        if (records.isEmpty()) {
            TextView empty = new TextView(this);
            empty.setText("暂无游戏记录，快去挑战一局吧！");
            empty.setTextSize(16f);
            empty.setTextColor(Color.WHITE);
            empty.setPadding(24, 24, 24, 24);
            list.addView(empty);
        } else {
            for (int i = 0; i < records.size(); i++) {
                ScoreRecord record = records.get(i);
                TextView row = new TextView(this);
                row.setText(String.format(java.util.Locale.CHINA, "%d.  %s    %d 分    %s",
                        i + 1, record.getUserName(), record.getScore(), record.getFormattedTime()));
                row.setTextSize(16f);
                row.setTextColor(Color.WHITE);
                row.setPadding(16, 18, 16, 18);
                list.addView(row, new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            }
        }

        Button back = findViewById(R.id.button_back);
        back.setOnClickListener(v -> finish());
    }
}
