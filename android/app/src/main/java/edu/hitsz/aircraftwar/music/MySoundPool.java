package edu.hitsz.aircraftwar.music;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.SoundPool;
import android.util.Log;

import java.util.HashMap;
import java.util.Map;

/**
 * 音效管理（实验3 p23 要求的 music 包下 MySoundPool）。
 * SoundPool 适合短促、响应要求高的音效：子弹击中、爆炸、拾取道具、游戏结束。
 */
public class MySoundPool {
    private static final String TAG = "MySoundPool";
    private static MySoundPool instance;

    private SoundPool soundPool;
    private final Map<Integer, Integer> soundIds = new HashMap<>();
    private boolean soundEnabled = true;

    private MySoundPool() {
    }

    public static synchronized MySoundPool getInstance() {
        if (instance == null) {
            instance = new MySoundPool();
        }
        return instance;
    }

    public void init(Context context) {
        if (soundPool != null) {
            return;
        }
        AudioAttributes attributes = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build();
        soundPool = new SoundPool.Builder()
                .setAudioAttributes(attributes)
                .setMaxStreams(6)
                .build();
    }

    public void setSoundEnabled(boolean enabled) {
        this.soundEnabled = enabled;
    }

    /** 预加载一份音效，返回 soundId */
    public int load(Context context, int resId) {
        if (soundPool == null) {
            init(context);
        }
        Integer cached = soundIds.get(resId);
        if (cached != null) {
            return cached;
        }
        int id = soundPool.load(context.getApplicationContext(), resId, 1);
        soundIds.put(resId, id);
        return id;
    }

    public void play(int soundId) {
        if (!soundEnabled || soundPool == null || soundId == 0) {
            return;
        }
        try {
            soundPool.play(soundId, 1.0f, 1.0f, 1, 0, 1.0f);
        } catch (Exception e) {
            Log.e(TAG, "播放音效失败", e);
        }
    }

    public void release() {
        if (soundPool != null) {
            soundPool.release();
            soundPool = null;
            soundIds.clear();
        }
    }
}
