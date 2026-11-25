package edu.hitsz.aircraftwar;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.InputType;
import android.util.Log;
import android.widget.EditText;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import edu.hitsz.aircraftwar.business.BaseGame;
import edu.hitsz.aircraftwar.business.EasyGame;
import edu.hitsz.aircraftwar.business.HardGame;
import edu.hitsz.aircraftwar.business.NormalGame;

/**
 * 游戏页面宿主：加载 SurfaceView 子类（实验2 p21），
 * 并在主线程处理「游戏结束」消息（实验3 p30-p32：子线程不能直接改 UI）。
 */
public class GameActivity extends AppCompatActivity {
    private static final String TAG = "GameActivity";

    private BaseGame game;
    private boolean gameOverHandled = false;

    private final Handler mainHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == BaseGame.MSG_GAME_OVER) {
                BaseGame.GameResult result = (BaseGame.GameResult) msg.obj;
                onGameOver(result);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String difficulty = getIntent().getStringExtra(MainActivity.EXTRA_DIFFICULTY);
        if (difficulty == null) {
            difficulty = MainActivity.DIFFICULTY_NORMAL;
        }
        switch (difficulty) {
            case MainActivity.DIFFICULTY_EASY:
                game = new EasyGame(this, mainHandler);
                break;
            case MainActivity.DIFFICULTY_HARD:
                game = new HardGame(this, mainHandler);
                break;
            case MainActivity.DIFFICULTY_NORMAL:
            default:
                game = new NormalGame(this, mainHandler);
                break;
        }
        setContentView(game);
        Log.i(TAG, "进入游戏界面，难度=" + difficulty);
    }

    /** 游戏结束：主线程弹窗输入姓名 → 保存成绩 → 跳转排行榜 */
    private void onGameOver(BaseGame.GameResult result) {
        if (gameOverHandled || isFinishing()) {
            return;
        }
        gameOverHandled = true;

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        input.setHint("输入姓名（留空则为匿名玩家）");

        new AlertDialog.Builder(this)
                .setTitle("游戏结束")
                .setMessage("本局得分：" + result.score)
                .setView(input)
                .setCancelable(false)
                .setPositiveButton("保存成绩", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String name = input.getText().toString();
                        game.saveRecord(name, result.score);
                        Intent intent = new Intent(GameActivity.this, RankActivity.class);
                        intent.putExtra(MainActivity.EXTRA_DIFFICULTY, result.difficulty);
                        startActivity(intent);
                        finish();
                    }
                })
                .show();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (game != null) {
            game.stopGame();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (game != null) {
            game.stopGame();
            game = null;
        }
    }
}
