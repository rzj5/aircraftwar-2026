package edu.hitsz.aircraftwar;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;

import androidx.appcompat.app.AppCompatActivity;

import edu.hitsz.aircraftwar.image.BitmapManager;
import edu.hitsz.aircraftwar.music.MyMediaPlayer;
import edu.hitsz.aircraftwar.music.MySoundPool;

/**
 * 难度选择页面（实验3 实验内容①②③）。
 * 桌面版对应 edu.hitsz.ui.StartMenuPanel：三个按钮直接开新 JPanel，
 * Android 侧改为 Intent 跳转到 GameActivity，并携带难度参数。
 */
public class MainActivity extends AppCompatActivity {

    public static final String EXTRA_DIFFICULTY = "extra_difficulty";
    public static final String DIFFICULTY_EASY = "easy";
    public static final String DIFFICULTY_NORMAL = "normal";
    public static final String DIFFICULTY_HARD = "hard";

    private Switch musicSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BitmapManager.load(this);
        MyMediaPlayer.getInstance().init(this);
        MySoundPool.getInstance().init(this);

        Button easyButton = findViewById(R.id.button_easy);
        Button normalButton = findViewById(R.id.button_normal);
        Button hardButton = findViewById(R.id.button_hard);
        Button rankButton = findViewById(R.id.button_rank);
        musicSwitch = findViewById(R.id.switch_music);

        easyButton.setOnClickListener(v -> startGame(DIFFICULTY_EASY));
        normalButton.setOnClickListener(v -> startGame(DIFFICULTY_NORMAL));
        hardButton.setOnClickListener(v -> startGame(DIFFICULTY_HARD));
        rankButton.setOnClickListener(v -> startActivity(new Intent(this, RankActivity.class)));

        musicSwitch.setChecked(MyMediaPlayer.getInstance().isMusicEnabled());
        musicSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                MyMediaPlayer.getInstance().setMusicEnabled(isChecked);
                MySoundPool.getInstance().setSoundEnabled(isChecked);
            }
        });
    }

    private void startGame(String difficulty) {
        Intent intent = new Intent(this, GameActivity.class);
        intent.putExtra(EXTRA_DIFFICULTY, difficulty);
        startActivity(intent);
    }
}
