package edu.hitsz.aircraftwar.business;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import edu.hitsz.aircraftwar.R;
import edu.hitsz.aircraftwar.aircraft.AbstractAircraft;
import edu.hitsz.aircraftwar.aircraft.BossEnemy;
import edu.hitsz.aircraftwar.aircraft.BossEnemyFactory;
import edu.hitsz.aircraftwar.aircraft.EliteEnemy;
import edu.hitsz.aircraftwar.aircraft.EliteEnemyFactory;
import edu.hitsz.aircraftwar.aircraft.ElitePlusEnemy;
import edu.hitsz.aircraftwar.aircraft.ElitePlusEnemyFactory;
import edu.hitsz.aircraftwar.aircraft.EnemyFactory;
import edu.hitsz.aircraftwar.aircraft.HeroAircraft;
import edu.hitsz.aircraftwar.aircraft.MobEnemyFactory;
import edu.hitsz.aircraftwar.aircraft.SuperBoss;
import edu.hitsz.aircraftwar.aircraft.SuperBossFactory;
import edu.hitsz.aircraftwar.basic.AbstractFlyingObject;
import edu.hitsz.aircraftwar.bullet.BaseBullet;
import edu.hitsz.aircraftwar.dao.ScoreDao;
import edu.hitsz.aircraftwar.dao.ScoreDaoImpl;
import edu.hitsz.aircraftwar.entity.ScoreRecord;
import edu.hitsz.aircraftwar.image.BitmapManager;
import edu.hitsz.aircraftwar.music.MyMediaPlayer;
import edu.hitsz.aircraftwar.music.MySoundPool;
import edu.hitsz.aircraftwar.prop.BaseProp;
import edu.hitsz.aircraftwar.prop.BombProp;
import edu.hitsz.aircraftwar.prop.FreezeProp;
import edu.hitsz.aircraftwar.prop.PropSimpleFactory;
import edu.hitsz.aircraftwar.util.GameConfig;

/**
 * 游戏主循环：由桌面版 JPanel + javax.swing.Timer 迁移为 SurfaceView + 子线程，
 * 对应实验2 p20 的五个步骤与 p23 的线程边界要求。
 *
 * 子线程只做「算 + 画」；游戏结束这类 UI 动作通过 Handler 发回主线程（实验3 p30-p32）。
 */
public abstract class BaseGame extends SurfaceView implements SurfaceHolder.Callback, Runnable {
    private static final String TAG = "BaseGame";

    /** 游戏结束消息：交给 GameActivity 在主线程处理（保存成绩、跳转排行榜） */
    public static final int MSG_GAME_OVER = 1;

    protected Bitmap background;
    protected final String gameDifficulty;
    protected ScoreDao scoreDao;
    protected final List<BaseProp> props = new ArrayList<>();
    protected int backGroundTop = 0;
    protected final HeroAircraft heroAircraft;
    protected final List<AbstractAircraft> enemyAircrafts = new LinkedList<>();
    protected final List<BaseBullet> heroBullets = new LinkedList<>();
    protected final List<BaseBullet> enemyBullets = new LinkedList<>();

    protected final EnemyFactory mobEnemyFactory = new MobEnemyFactory();
    protected final EnemyFactory eliteEnemyFactory = new EliteEnemyFactory();
    protected final EnemyFactory elitePlusEnemyFactory = new ElitePlusEnemyFactory();
    protected final EnemyFactory bossEnemyFactory = new BossEnemyFactory();
    protected SuperBossFactory superBossFactory;

    protected int enemyMaxNumber = 5;
    protected double enemySpawnCycle = 20;
    protected int enemySpawnCounter = 0;
    protected double shootCycle = 20;
    protected int shootCounter = 0;
    protected int score = 0;
    protected boolean superBossAlive = false;
    protected boolean gameOverFlag = false;
    protected static final int SUPER_BOSS_SCORE = 200;

    private static final long TICK_INTERVAL = 40L;

    private SurfaceHolder holder;
    private Thread gameThread;
    private volatile boolean running = false;

    private int surfaceWidth = 0;
    private int surfaceHeight = 0;
    private float scale = 1f;
    private float offsetX = 0f;
    private float offsetY = 0f;

    private final Paint textPaint = new Paint();
    private final Handler mainHandler;

    private int soundBulletHit;
    private int soundBomb;
    private int soundGetSupply;
    private int soundGameOver;

    public BaseGame(Context context, String difficulty, Handler mainHandler) {
        super(context);
        this.gameDifficulty = difficulty;
        this.mainHandler = mainHandler;
        this.superBossFactory = new SuperBossFactory();

        BitmapManager.load(context);
        MyMediaPlayer.getInstance().init(context);
        MySoundPool soundPool = MySoundPool.getInstance();
        soundPool.init(context);
        soundBulletHit = soundPool.load(context, R.raw.bullet_hit);
        soundBomb = soundPool.load(context, R.raw.bomb_explosion);
        soundGetSupply = soundPool.load(context, R.raw.get_supply);
        soundGameOver = soundPool.load(context, R.raw.game_over);

        heroAircraft = HeroAircraft.getInstance(
                GameConfig.VIRTUAL_WIDTH / 2,
                GameConfig.VIRTUAL_HEIGHT - BitmapManager.HERO.getHeight(),
                0, 0, 100);
        heroAircraft.reset();

        scoreDao = new ScoreDaoImpl(context, difficulty);
        initBackground();

        textPaint.setColor(Color.RED);
        textPaint.setAntiAlias(true);
        textPaint.setTextSize(22f);

        holder = getHolder();
        holder.addCallback(this);
        setFocusable(true);
        setKeepScreenOn(true);
        initTouchControl();
    }

    protected abstract void initBackground();

    public abstract boolean allowSuperBoss();

    public abstract void increaseDifficulty();

    /**
     * 触屏控制：替代桌面版鼠标拖拽（实验2 p19 的 HeroController 迁移）。
     * 手指按下与移动都直接把英雄机中心对准触点，并做边界裁剪。
     */
    private void initTouchControl() {
        setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getActionMasked()) {
                    case MotionEvent.ACTION_DOWN:
                    case MotionEvent.ACTION_MOVE:
                        moveHeroTo(motionEvent.getX(), motionEvent.getY());
                        return true;
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                        return true;
                    default:
                        return false;
                }
            }
        });
    }

    private void moveHeroTo(float screenX, float screenY) {
        if (scale <= 0) {
            return;
        }
        int vx = (int) ((screenX - offsetX) / scale);
        int vy = (int) ((screenY - offsetY) / scale);
        int halfW = Math.max(1, heroAircraft.getWidth() / 2);
        int halfH = Math.max(1, heroAircraft.getHeight() / 2);
        vx = Math.max(halfW, Math.min(GameConfig.VIRTUAL_WIDTH - halfW, vx));
        vy = Math.max(halfH, Math.min(GameConfig.VIRTUAL_HEIGHT - halfH, vy));
        heroAircraft.setLocation(vx, vy);
    }

    // ---------------- SurfaceHolder.Callback ----------------

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        // 进入游戏即播放 BGM（实验3 p22：bgm.wav 为游戏背景音乐）
        MyMediaPlayer.getInstance().play(R.raw.bgm);
        running = true;
        gameThread = new Thread(this, "game-loop");
        gameThread.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        this.surfaceWidth = width;
        this.surfaceHeight = height;
        // 按真实宽高等比缩放虚拟坐标系，避免写死像素（实验2 p24 第③个坑）
        float sx = width / (float) GameConfig.VIRTUAL_WIDTH;
        float sy = height / (float) GameConfig.VIRTUAL_HEIGHT;
        scale = Math.min(sx, sy);
        offsetX = (width - GameConfig.VIRTUAL_WIDTH * scale) / 2f;
        offsetY = (height - GameConfig.VIRTUAL_HEIGHT * scale) / 2f;
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        // 置标志位并等线程结束，避免退出后仍在绘制（实验2 p24 第④个坑）
        running = false;
        if (gameThread != null) {
            try {
                gameThread.join(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            gameThread = null;
        }
        MyMediaPlayer.getInstance().stop();
    }

    // ---------------- 游戏循环（子线程） ----------------

    @Override
    public void run() {
        while (running) {
            long start = System.currentTimeMillis();
            if (!gameOverFlag) {
                update();
            }
            drawFrame();
            long cost = System.currentTimeMillis() - start;
            long sleep = TICK_INTERVAL - cost;
            if (sleep > 0) {
                try {
                    Thread.sleep(sleep);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }
    }

    private void update() {
        propsUpdate();
        enemySpawnProcess();
        shootProcess();
        bulletsMoveAction();
        aircraftsMoveAction();
        crashCheckAction();
        postProcessAction();
    }

    private void drawFrame() {
        Canvas canvas = null;
        try {
            canvas = holder.lockCanvas();
            if (canvas == null) {
                return;
            }
            canvas.drawColor(Color.BLACK);
            canvas.save();
            canvas.translate(offsetX, offsetY);
            canvas.scale(scale, scale);
            drawScene(canvas);
            canvas.restore();
        } catch (Exception e) {
            Log.e(TAG, "绘制帧失败", e);
        } finally {
            if (canvas != null) {
                try {
                    // 必须成对调用，漏掉会黑屏（实验2 p25）
                    holder.unlockCanvasAndPost(canvas);
                } catch (Exception e) {
                    Log.e(TAG, "unlockCanvasAndPost 失败", e);
                }
            }
        }
    }

    private void drawScene(Canvas canvas) {
        if (background != null) {
            canvas.drawBitmap(background, null,
                    new Rect(0, backGroundTop - GameConfig.VIRTUAL_HEIGHT,
                            GameConfig.VIRTUAL_WIDTH, backGroundTop), null);
            canvas.drawBitmap(background, null,
                    new Rect(0, backGroundTop,
                            GameConfig.VIRTUAL_WIDTH, backGroundTop + GameConfig.VIRTUAL_HEIGHT), null);
            backGroundTop++;
            if (backGroundTop >= GameConfig.VIRTUAL_HEIGHT) {
                backGroundTop = 0;
            }
        }
        paintObjects(canvas, enemyBullets);
        paintObjects(canvas, heroBullets);
        paintObjects(canvas, enemyAircrafts);
        paintObjects(canvas, props);
        if (BitmapManager.HERO != null) {
            canvas.drawBitmap(BitmapManager.HERO,
                    heroAircraft.getLocationX() - BitmapManager.HERO.getWidth() / 2f,
                    heroAircraft.getLocationY() - BitmapManager.HERO.getHeight() / 2f, null);
        }
        canvas.drawText("SCORE: " + score, 10, 25, textPaint);
        canvas.drawText("LIFE: " + heroAircraft.getHp(), 10, 45, textPaint);
    }

    private void paintObjects(Canvas canvas, List<? extends AbstractFlyingObject> list) {
        for (AbstractFlyingObject o : list) {
            Bitmap img = o.getImage();
            if (img == null) {
                continue;
            }
            canvas.drawBitmap(img,
                    o.getLocationX() - img.getWidth() / 2f,
                    o.getLocationY() - img.getHeight() / 2f, null);
        }
    }

    // ---------------- 逻辑（与桌面版逐行一致，仅去掉 Swing 依赖） ----------------

    private void propsUpdate() {
        Iterator<BaseProp> it = props.iterator();
        while (it.hasNext()) {
            BaseProp prop = it.next();
            prop.forward();
            if (prop.notValid()) {
                it.remove();
            }
        }
    }

    private void enemySpawnProcess() {
        enemySpawnCounter++;
        if (allowSuperBoss() && score >= SUPER_BOSS_SCORE && !superBossAlive) {
            try {
                MyMediaPlayer.getInstance().play(R.raw.bgm_boss);
                enemyAircrafts.add(superBossFactory.createEnemy());
                superBossAlive = true;
            } catch (Exception e) {
                Log.e(TAG, "生成超级 Boss 失败", e);
            }
        }

        if (enemySpawnCounter >= enemySpawnCycle) {
            enemySpawnCounter = 0;
            double random = Math.random();
            if (random < 0.5) {
                enemyAircrafts.add(mobEnemyFactory.createEnemy());
            } else if (random < 0.8) {
                enemyAircrafts.add(eliteEnemyFactory.createEnemy());
            } else if (random < 0.95) {
                enemyAircrafts.add(elitePlusEnemyFactory.createEnemy());
            } else {
                enemyAircrafts.add(bossEnemyFactory.createEnemy());
            }
            increaseDifficulty();
        }
    }

    private void shootProcess() {
        shootCounter++;
        if (shootCounter >= shootCycle) {
            shootCounter = 0;
            heroBullets.addAll(heroAircraft.shoot());
            for (AbstractAircraft enemy : enemyAircrafts) {
                enemyBullets.addAll(enemy.shoot());
            }
        }
    }

    protected void bulletsMoveAction() {
        for (BaseBullet b : heroBullets) {
            b.forward();
        }
        for (BaseBullet b : enemyBullets) {
            b.forward();
        }
    }

    protected void aircraftsMoveAction() {
        for (AbstractAircraft e : enemyAircrafts) {
            e.forward();
        }
    }

    protected void crashCheckAction() {
        // 1. 英雄子弹命中敌机
        for (BaseBullet bullet : heroBullets) {
            for (AbstractAircraft enemy : enemyAircrafts) {
                if (bullet.crash(enemy) && bullet.isValid() && enemy.isValid()) {
                    bullet.vanish();
                    enemy.decreaseHp(bullet.getPower());
                    MySoundPool.getInstance().play(soundBulletHit);
                    if (enemy.getHp() <= 0) {
                        enemy.vanish();
                        score += getEnemyScore(enemy);
                        dropProp(enemy);
                        if (enemy instanceof SuperBoss) {
                            MyMediaPlayer.getInstance().play(R.raw.bgm);
                            superBossAlive = false;
                        }
                    }
                }
            }
        }

        // 2. 敌机子弹命中英雄机
        for (BaseBullet bullet : enemyBullets) {
            if (bullet.isValid() && bullet.crash(heroAircraft)) {
                bullet.vanish();
                heroAircraft.decreaseHp(bullet.getPower());
                MySoundPool.getInstance().play(soundBulletHit);
                if (heroAircraft.getHp() <= 0) {
                    gameOverFlag = true;
                }
            }
        }

        // 3. 英雄机与敌机相撞
        for (AbstractAircraft enemy : enemyAircrafts) {
            if (enemy.isValid() && enemy.crash(heroAircraft)) {
                enemy.vanish();
                heroAircraft.decreaseHp(Integer.MAX_VALUE);
                gameOverFlag = true;
            }
        }

        // 4. 英雄机拾取道具
        Iterator<BaseProp> it = props.iterator();
        while (it.hasNext()) {
            BaseProp prop = it.next();
            if (prop.crash(heroAircraft) && prop.isValid()) {
                prop.vanish();
                it.remove();
                handleProp(prop);
            }
        }

        if (gameOverFlag) {
            onGameOver();
        }
    }

    private int getEnemyScore(AbstractAircraft e) {
        if (e instanceof EliteEnemy) {
            return 10;
        }
        if (e instanceof ElitePlusEnemy) {
            return 30;
        }
        if (e instanceof BossEnemy) {
            return 100;
        }
        if (e instanceof SuperBoss) {
            return 300;
        }
        return 10;
    }

    private void dropProp(AbstractAircraft e) {
        int x = e.getLocationX();
        int y = e.getLocationY();
        double r = Math.random();
        if (e instanceof EliteEnemy) {
            if (r < 0.3) {
                createProp(x, y);
            }
        } else if (e instanceof ElitePlusEnemy) {
            if (r < 0.6) {
                createProp(x, y);
            }
        } else if (e instanceof BossEnemy) {
            createProp(x, y);
        } else if (e instanceof SuperBoss) {
            for (int i = 0; i < 3; i++) {
                createProp(x + (i - 1) * 40, y);
            }
        }
    }

    private void createProp(int x, int y) {
        double r = Math.random();
        String t;
        if (r < 0.2) {
            t = "hp";
        } else if (r < 0.4) {
            t = "fire";
        } else if (r < 0.6) {
            t = "firePlus";
        } else if (r < 0.8) {
            t = "bomb";
        } else {
            t = "freeze";
        }
        BaseProp p = PropSimpleFactory.createProp(t, x, y, 0, 6);
        if (p != null) {
            props.add(p);
        }
    }

    private void handleProp(BaseProp p) {
        if (p instanceof BombProp) {
            MySoundPool.getInstance().play(soundBomb);
            BombProp bp = (BombProp) p;
            for (AbstractAircraft e : enemyAircrafts) {
                bp.addObserver(e);
            }
            for (BaseBullet b : enemyBullets) {
                bp.addObserver(b);
            }
            bp.notifyBomb();
        } else if (p instanceof FreezeProp) {
            MySoundPool.getInstance().play(soundGetSupply);
            FreezeProp fp = (FreezeProp) p;
            for (AbstractAircraft e : enemyAircrafts) {
                fp.addObserver(e);
            }
            for (BaseBullet b : enemyBullets) {
                fp.addObserver(b);
            }
            fp.notifyFreeze();
        } else {
            MySoundPool.getInstance().play(soundGetSupply);
            p.active(heroAircraft);
        }
    }

    protected void postProcessAction() {
        enemyBullets.removeIf(AbstractFlyingObject::notValid);
        heroBullets.removeIf(AbstractFlyingObject::notValid);
        enemyAircrafts.removeIf(AbstractFlyingObject::notValid);
        props.removeIf(AbstractFlyingObject::notValid);
    }

    /**
     * 游戏结束：子线程不能直接改 UI，通过 Handler 交给主线程（实验3 p30-p32）。
     */
    private void onGameOver() {
        running = false;
        MyMediaPlayer.getInstance().stop();
        MySoundPool.getInstance().play(soundGameOver);
        Message message = mainHandler.obtainMessage(MSG_GAME_OVER);
        message.obj = new GameResult(score, gameDifficulty);
        mainHandler.sendMessage(message);
    }

    /** 游戏结果：传给主线程保存成绩、跳转排行榜 */
    public static class GameResult {
        public final int score;
        public final String difficulty;

        public GameResult(int score, String difficulty) {
            this.score = score;
            this.difficulty = difficulty;
        }
    }

    public void saveRecord(String playerName, int finalScore) {
        String name = (playerName == null || playerName.trim().isEmpty()) ? "匿名玩家" : playerName.trim();
        scoreDao.addRecord(new ScoreRecord(name, finalScore, new java.util.Date(), gameDifficulty));
        scoreDao.printRankingList();
    }

    public void stopGame() {
        running = false;
        if (gameThread != null) {
            try {
                gameThread.join(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            gameThread = null;
        }
        MyMediaPlayer.getInstance().stop();
    }

    public int getScore() {
        return score;
    }

    public String getGameDifficulty() {
        return gameDifficulty;
    }

    public boolean isGameOver() {
        return gameOverFlag;
    }

    protected void setBackgroundBitmap(Bitmap bitmap) {
        this.background = bitmap;
    }
}
