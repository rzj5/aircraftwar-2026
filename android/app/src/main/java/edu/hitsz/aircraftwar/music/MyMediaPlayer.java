package edu.hitsz.aircraftwar.music;

import android.content.Context;
import android.media.MediaPlayer;
import android.util.Log;

/**
 * 背景音乐管理（实验3 p22 要求的 music 包下 MyMediaPlayer）。
 * MediaPlayer 适合长时间、时效要求低的音频：普通 BGM 与 Boss BGM。
 * boss BGM 播放前先暂停普通 BGM，boss 被击落后切回普通 BGM。
 */
public class MyMediaPlayer {
    private static final String TAG = "MyMediaPlayer";
    private static MyMediaPlayer instance;

    private MediaPlayer player;
    private int currentResId = 0;
    private boolean musicEnabled = true;
    private Context appContext;

    private MyMediaPlayer() {
    }

    public static synchronized MyMediaPlayer getInstance() {
        if (instance == null) {
            instance = new MyMediaPlayer();
        }
        return instance;
    }

    public void init(Context context) {
        if (appContext == null) {
            appContext = context.getApplicationContext();
        }
    }

    public void setMusicEnabled(boolean enabled) {
        this.musicEnabled = enabled;
        if (!enabled) {
            pause();
        }
    }

    public boolean isMusicEnabled() {
        return musicEnabled;
    }

    /** 播放循环 BGM：先停掉当前曲目，再播放新曲目 */
    public void play(int resId) {
        if (appContext == null || !musicEnabled) {
            return;
        }
        if (currentResId == resId && player != null && player.isPlaying()) {
            return;
        }
        stop();
        currentResId = resId;
        try {
            player = MediaPlayer.create(appContext, resId);
            if (player == null) {
                Log.e(TAG, "MediaPlayer.create 返回 null, resId=" + resId);
                return;
            }
            player.setLooping(true);
            player.start();
        } catch (Exception e) {
            Log.e(TAG, "播放 BGM 失败", e);
        }
    }

    public void pause() {
        try {
            if (player != null && player.isPlaying()) {
                player.pause();
            }
        } catch (Exception e) {
            Log.e(TAG, "暂停 BGM 失败", e);
        }
    }

    public void stop() {
        try {
            if (player != null) {
                if (player.isPlaying()) {
                    player.stop();
                }
                player.release();
            }
        } catch (Exception e) {
            Log.e(TAG, "停止 BGM 失败", e);
        } finally {
            player = null;
            currentResId = 0;
        }
    }

    public void release() {
        stop();
    }
}
