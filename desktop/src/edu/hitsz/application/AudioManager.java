package edu.hitsz.application;

import javax.sound.sampled.*;
import java.io.IOException;
import java.io.InputStream;

/**
 * 音频音效管理器，完全适配你项目的src/videos路径
 */
public class AudioManager {
    // 单例实例
    private static AudioManager instance;
    // 背景音乐Clip（支持普通BGM和Boss战BGM切换）
    private Clip bgmClip;

    // 音效资源路径（和你src/videos下的文件100%对应）
    public static final String BGM = "/videos/bgm.wav";
    public static final String BGM_BOSS = "/videos/bgm_boss.wav";
    public static final String BOMB_EXPLOSION = "/videos/bomb_explosion.wav";
    public static final String BULLET_HIT = "/videos/bullet_hit.wav";
    public static final String GAME_OVER = "/videos/game_over.wav";
    public static final String GET_SUPPLY = "/videos/get_supply.wav";

    private AudioManager() {}

    // 音频文件既可能已随源码复制进 classpath，也可能只留在 src/ 目录。
    // 找不到 classpath 资源时回退到 src/ 下的同名相对路径，保证纯 javac 运行也能播放音效。
    private InputStream openAudioStream(String filePath) {
        InputStream stream = AudioManager.class.getResourceAsStream(filePath);
        if (stream != null) {
            // 音频解码器需要 mark/reset，统一用缓冲流包装
            return stream.markSupported() ? stream : new java.io.BufferedInputStream(stream);
        }
        if (filePath != null && filePath.startsWith("/")) {
            String relative = "src" + filePath;
            try {
                return new java.io.BufferedInputStream(new java.io.FileInputStream(relative));
            } catch (java.io.FileNotFoundException e) {
                System.err.println("音频资源未找到: " + filePath + " (尝试 " + relative + ")");
            }
        }
        return null;
    }

    // 获取单例
    public static AudioManager getInstance() {
        if (instance == null) {
            instance = new AudioManager();
        }
        return instance;
    }

    /**
     * 播放循环背景音乐（普通BGM/Boss战BGM通用）
     */
    public void playBGM(String filePath) {
        try {
            stopBGM(); // 先停止当前BGM，再播放新的
            InputStream audioStream = openAudioStream(filePath);
            if (audioStream == null) {
                return;
            }
            AudioInputStream audioIn = AudioSystem.getAudioInputStream(audioStream);
            bgmClip = AudioSystem.getClip();
            bgmClip.open(audioIn);
            bgmClip.loop(Clip.LOOP_CONTINUOUSLY); // 循环播放
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace();
        }
    }

    /**
     * 停止当前背景音乐（切换BGM或游戏结束时调用）
     */
    public void stopBGM() {
        if (bgmClip != null && bgmClip.isRunning()) {
            bgmClip.stop();
            bgmClip.close();
        }
    }

    /**
     * 播放一次性音效（所有非循环音效都用这个方法）
     */
    public void playSoundEffect(String filePath) {
        new Thread(() -> {
            try {
                InputStream audioStream = openAudioStream(filePath);
                if (audioStream == null) {
                    return;
                }
                AudioInputStream audioIn = AudioSystem.getAudioInputStream(audioStream);
                Clip clip = AudioSystem.getClip();
                clip.open(audioIn);
                clip.start();
            } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
                e.printStackTrace();
            }
        }).start();
    }
}
