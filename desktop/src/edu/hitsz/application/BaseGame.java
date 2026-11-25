package edu.hitsz.application;

import edu.hitsz.ui.RankPanel;
import edu.hitsz.aircraft.*;
import edu.hitsz.bullet.BaseBullet;
import edu.hitsz.basic.AbstractFlyingObject;
import edu.hitsz.dao.ScoreDao;
import edu.hitsz.dao.ScoreDaoImpl;
import edu.hitsz.entity.ScoreRecord;
import java.time.LocalDateTime;
import edu.hitsz.prop.*;
import edu.hitsz.ui.GameFrame;
import edu.hitsz.application.HeroController;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.*;
import java.util.List;
import java.util.Timer;

public abstract class BaseGame extends JPanel {
    protected BufferedImage background;
    protected final String gameDifficulty;
    protected ScoreDao scoreDao;
    protected final List<BaseProp> props = new ArrayList<>();
    protected int backGroundTop = 0;
    protected final Timer timer;
    protected final int timeInterval = 40;
    protected final HeroAircraft heroAircraft;
    public final List<AbstractAircraft> enemyAircrafts;
    protected final List<BaseBullet> heroBullets;
    protected final List<BaseBullet> enemyBullets;

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

    public BaseGame(String difficulty) {
        this.gameDifficulty = difficulty;
        this.superBossFactory = new SuperBossFactory();
        heroAircraft = HeroAircraft.getInstance(
                Main.WINDOW_WIDTH / 2,
                Main.WINDOW_HEIGHT - ImageManager.HERO_IMAGE.getHeight(),
                0, 0, 100);
        enemyAircrafts = new LinkedList<>();
        heroBullets = new LinkedList<>();
        enemyBullets = new LinkedList<>();
        scoreDao = new ScoreDaoImpl(gameDifficulty);
        new HeroController(this, heroAircraft);
        this.timer = new Timer("game-action-timer", true);
        AudioManager.getInstance().playBGM(AudioManager.BGM);
        HeroAircraft.getInstance().reset();
        initBackground();
    }

    protected abstract void initBackground();
    public abstract boolean allowSuperBoss();
    public abstract void increaseDifficulty();

    public void action() {
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                if (gameOverFlag) {
                    timer.cancel();
                    return;
                }
                propsUpdate();
                enemySpawnProcess();
                shootProcess();
                bulletsMoveAction();
                aircraftsMoveAction();
                crashCheckAction();
                postProcessAction();
                repaint();
                checkResultAction();
            }
        };
        timer.schedule(task, 0, timeInterval);
    }

    private void propsUpdate() {
        Iterator<BaseProp> propIterator = props.iterator();
        while (propIterator.hasNext()) {
            BaseProp prop = propIterator.next();
            prop.forward();
            if (prop.notValid()) propIterator.remove();
        }
    }

    private void enemySpawnProcess() {
        enemySpawnCounter++;
        if (allowSuperBoss() && score >= SUPER_BOSS_SCORE && !superBossAlive) {
            try {
                AudioManager.getInstance().playBGM(AudioManager.BGM_BOSS);
                AbstractAircraft superBoss = superBossFactory.createEnemy();
                enemyAircrafts.add(superBoss);
                superBossAlive = true;
            } catch (Exception e) {e.printStackTrace();}
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
                List<BaseBullet> bs = enemy.shoot();
                enemyBullets.addAll(bs);
            }
        }
    }

    protected void bulletsMoveAction() {
        for (BaseBullet bullet : heroBullets) bullet.forward();
        for (BaseBullet bullet : enemyBullets) bullet.forward();
    }

    protected void aircraftsMoveAction() {
        for (AbstractAircraft e : enemyAircrafts) e.forward();
    }

    protected void crashCheckAction() {
        // 1. 英雄子弹撞敌机（原有逻辑不变）
        for (BaseBullet bullet : heroBullets) {
            for (AbstractAircraft enemy : enemyAircrafts) {
                if (bullet.crash(enemy)) {
                    bullet.vanish();
                    enemy.decreaseHp(bullet.getPower());
                    if (enemy.getHp() <= 0) {
                        enemy.vanish();
                        score += getEnemyScore(enemy);
                        dropProp(enemy);
                        if (enemy instanceof SuperBoss) {
                            AudioManager.getInstance().playBGM(AudioManager.BGM);
                            superBossAlive = false;
                        }
                    }
                }
            }
        }

        // 2. 敌机子弹撞英雄机（原有逻辑不变）
        for (BaseBullet bullet : enemyBullets) {
            if (bullet.crash(heroAircraft)) {
                bullet.vanish();
                heroAircraft.decreaseHp(bullet.getPower());
                if (heroAircraft.getHp() <= 0) {
                    gameOverFlag = true;
                }
            }
        }

        // 3. 英雄机撞敌机（原有逻辑不变）
        for (AbstractAircraft enemy : enemyAircrafts) {
            if (enemy.crash(heroAircraft)) {
                enemy.vanish();
                heroAircraft.decreaseHp(Integer.MAX_VALUE);
                gameOverFlag = true;
            }
        }

        // 4. 英雄机吃道具（包含炸弹道具的清屏逻辑，这就是你没找到的部分）
        Iterator<BaseProp> pIter = props.iterator();
        while (pIter.hasNext()) {
            BaseProp prop = pIter.next();
            if (prop.crash(heroAircraft) && prop.isValid()) {
                prop.vanish();
                pIter.remove();
                handleProp(prop);
            }
        }
    }


    private int getEnemyScore(AbstractAircraft e) {
        if (e instanceof EliteEnemy) return 10;
        if (e instanceof ElitePlusEnemy) return 30;
        if (e instanceof BossEnemy) return 100;
        if (e instanceof SuperBoss) return 300;
        return 10;
    }

    private void dropProp(AbstractAircraft e) {
        int x = e.getLocationX();
        int y = e.getLocationY();
        double r = Math.random();
        if (e instanceof EliteEnemy) {
            if (r < 0.3) createProp(x, y);
        } else if (e instanceof ElitePlusEnemy) {
            if (r < 0.6) createProp(x, y);
        } else if (e instanceof BossEnemy) {
            createProp(x, y);
        } else if (e instanceof SuperBoss) {
            for (int i = 0; i < 3; i++) createProp(x + (i - 1) * 40, y);
        }
    }

    private void createProp(int x, int y) {
        double r = Math.random();
        String t;
        if (r < 0.2) t = "hp";
        else if (r < 0.4) t = "fire";
        else if (r < 0.6) t = "firePlus";
        else if (r < 0.8) t = "bomb";
        else t = "freeze";
        BaseProp p = PropSimpleFactory.createProp(t, x, y, 0, 6);
        if (p != null) props.add(p);
    }

    private void handleProp(BaseProp p) {
        System.out.println("【道具真实类型】：" + p.getClass().getSimpleName());
        if (p instanceof BombProp) {
            // 炸弹逻辑（正常保留）
            AudioManager.getInstance().playSoundEffect(AudioManager.BOMB_EXPLOSION);
            BombProp bp = (BombProp) p;
            for (AbstractAircraft e : enemyAircrafts) bp.addObserver(e);
            for (BaseBullet b : enemyBullets) bp.addObserver(b);
            bp.notifyBomb();
        }
        else if (p instanceof FreezeProp) {
            //  完全恢复原来的冰冻调用，1:1还原
            System.out.println("✅ 进入冰冻道具分支！");
            AudioManager.getInstance().playSoundEffect(AudioManager.GET_SUPPLY);
            FreezeProp fp = (FreezeProp) p;
            for (AbstractAircraft e : enemyAircrafts) fp.addObserver(e);
            for (BaseBullet b : enemyBullets) fp.addObserver(b);
            fp.notifyFreeze(); // 通知冰冻，触发子类自己的onFreezeEffect ✅
        }
        else {
            AudioManager.getInstance().playSoundEffect(AudioManager.GET_SUPPLY);
            p.active(heroAircraft);
        }
    }





    protected void postProcessAction() {
        enemyBullets.removeIf(o -> o.notValid());
        heroBullets.removeIf(o -> o.notValid());
        enemyAircrafts.removeIf(o -> o.notValid());
        props.removeIf(o -> o.notValid());
    }

    protected void checkResultAction() {
        if (heroAircraft.getHp() <= 0) {
            AudioManager.getInstance().stopBGM();
            AudioManager.getInstance().playSoundEffect(AudioManager.GAME_OVER);
            timer.cancel();
            gameOverFlag = true;
            String name = JOptionPane.showInputDialog(this, "游戏结束！输入姓名：", "保存成绩", JOptionPane.PLAIN_MESSAGE);
            if (name == null || name.isBlank()) name = "匿名玩家";
            scoreDao.addRecord(new ScoreRecord(name.trim(), score, LocalDateTime.now(), gameDifficulty));
            scoreDao.printRankingList();
            RankPanel rp = new RankPanel(gameDifficulty);
            GameFrame.cardPanel.add(rp.getMainPanel(), GameFrame.RANK_PANEL);
            GameFrame.getInstance().showPanel(GameFrame.RANK_PANEL);
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(background, 0, backGroundTop - Main.WINDOW_HEIGHT, null);
        g.drawImage(background, 0, backGroundTop, null);
        backGroundTop++;
        if (backGroundTop == Main.WINDOW_HEIGHT) backGroundTop = 0;
        paintImage(g, enemyBullets);
        paintImage(g, heroBullets);
        paintImage(g, enemyAircrafts);
        paintImage(g, props);
        g.drawImage(ImageManager.HERO_IMAGE,
                heroAircraft.getLocationX() - ImageManager.HERO_IMAGE.getWidth() / 2,
                heroAircraft.getLocationY() - ImageManager.HERO_IMAGE.getHeight() / 2, null);
        paintScore(g);
    }

    private void paintImage(Graphics g, List<? extends AbstractFlyingObject> list) {
        for (AbstractFlyingObject o : list) {
            BufferedImage img = o.getImage();
            g.drawImage(img, o.getLocationX() - img.getWidth() / 2,
                    o.getLocationY() - img.getHeight() / 2, null);
        }
    }

    private void paintScore(Graphics g) {
        g.setColor(Color.RED);
        g.setFont(new Font("SansSerif", Font.BOLD, 22));
        g.drawString("SCORE: " + score, 10, 25);
        g.drawString("LIFE: " + heroAircraft.getHp(), 10, 45);
    }

    public List<BaseBullet> getHeroBullets() {return heroBullets;}
    public List<BaseBullet> getEnemyBullets() {return enemyBullets;}
    public List<BaseProp> getProps() {return props;}
}