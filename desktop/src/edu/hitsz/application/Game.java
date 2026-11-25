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

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.*;
import java.util.List;
import java.util.Timer;
import java.util.concurrent.*;

/**
 * 游戏主面板，游戏启动
 * @author hitsz
 */
public class Game extends JPanel {
    // 背景图（不同难度对应不同图片）
    private BufferedImage background;
    // 游戏难度
    private final String gameDifficulty ;
    // 分数DAO实例
    private ScoreDao scoreDao;

    // 新增：道具集合，管理所有屏幕内的道具
    private final List<BaseProp> props = new ArrayList<>();

    private int backGroundTop = 0;

    //调度器, 用于定时任务调度
    private final Timer timer;
    //时间间隔(ms)，控制刷新频率
    private final int timeInterval = 40;

    private final HeroAircraft heroAircraft;
    private final List<AbstractAircraft> enemyAircrafts;
    private final List<BaseBullet> heroBullets;
    private final List<BaseBullet> enemyBullets;
    // ========== 新增：敌机工厂实例（单例复用，避免每次生成都new工厂） ==========
    private final EnemyFactory mobEnemyFactory = new MobEnemyFactory();
    private final EnemyFactory eliteEnemyFactory = new EliteEnemyFactory();
    private final EnemyFactory elitePlusEnemyFactory = new ElitePlusEnemyFactory();
    private final EnemyFactory bossEnemyFactory = new BossEnemyFactory();



    //屏幕中出现的敌机最大数量
    private final int enemyMaxNumber = 5;

    //敌机生成周期
    protected double enemySpawnCycle = 20;
    private int enemySpawnCounter = 0;

    //英雄机和敌机射击周期
    protected double shootCycle = 20;
    private int shootCounter = 0;

    //当前玩家分数
    private int score = 0;
    // 1. 超级Boss工厂，用于创建SuperBoss实例
    private SuperBossFactory superBossFactory;
    // 2. 标记：场上是否存在SuperBoss，防止重复生成
    private boolean superBossAlive = false;

    //游戏结束标志
    private boolean gameOverFlag = false;


    public Game(String difficulty) {
        this.gameDifficulty = difficulty;
        // 【新增】不同难度替换对应背景图
        switch (difficulty) {
            case "easy":
                background = ImageManager.BG_EASY;
                break;
            case "hard":
                background = ImageManager.BG_HARD;
                break;
            default: // normal普通模式
               background = ImageManager.BG_NORMAL;
                break;
        }
// 【新增】打印日志，定位问题
        System.out.println("【调试】当前选择的难度：" + difficulty);
        System.out.println("【调试】当前设置的背景图：" + background);
        // 【新增】初始化实验四的ScoreDao，传入当前难度
        this.scoreDao = new ScoreDaoImpl(gameDifficulty);


    superBossFactory = new SuperBossFactory();
        heroAircraft = HeroAircraft.getInstance(
                Main.WINDOW_WIDTH / 2,
                Main.WINDOW_HEIGHT - ImageManager.HERO_IMAGE.getHeight(),
                0, 0, 100);

        enemyAircrafts = new LinkedList<>();
        heroBullets = new LinkedList<>();
        enemyBullets = new LinkedList<>();
        superBossFactory = new SuperBossFactory();
        scoreDao = new ScoreDaoImpl(gameDifficulty);
        //启动英雄机鼠标监听
        new HeroController(this, heroAircraft);

        this.timer = new Timer("game-action-timer", true);
        AudioManager.getInstance().playBGM(AudioManager.BGM);
        HeroAircraft.getInstance().reset();
    }

    /**
     * 游戏启动入口，执行游戏逻辑
     */
    public void action() {

        // 定时任务：绘制、对象产生、碰撞判定、及结束判定
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                if (gameOverFlag) {
                    timer.cancel();
                    return;
                }
// ========== 道具更新逻辑 ==========
                Iterator<BaseProp> propIterator = props.iterator();
                while (propIterator.hasNext()) {
                    BaseProp prop = propIterator.next();
                    prop.forward();
                    // 清理出界/失效的道具
                    if (prop.notValid()) {
                        propIterator.remove();
                    }
                }


                // ========== 敌机周期生成逻辑（完整正确版） ==========
                enemySpawnCounter++;
                // ===================== 分数阈值生成 SuperBoss（和Mob工厂完全匹配） =====================
                final int SUPER_BOSS_SCORE = 200; // 你可以自行修改分数阈值
                // 条件：分数达标 且 场上没有SuperBoss
                if (score >= SUPER_BOSS_SCORE && !superBossAlive) {
                    try {
                        // 1. 切换为Boss战BGM
                        AudioManager.getInstance().playBGM(AudioManager.BGM_BOSS);
                        // 用和MobEnemyFactory一样的工厂方法创建实例
                        AbstractAircraft superBoss = superBossFactory.createEnemy();

                        // 加入敌机集合，开始渲染和逻辑循环
                        enemyAircrafts.add(superBoss);

                        // 标记为已生成，防止重复创建
                        superBossAlive = true;

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                // =========================================================================================

// 达到设定的间隔时间，才生成一次敌机，避免满屏敌机
                if (enemySpawnCounter >= shootCycle) {
                    enemySpawnCounter = 0; // 重置计数器
                    // 【关键修复】先定义并初始化random变量，再在if里使用
                    double random = Math.random(); // 生成0~1之间的随机数

                    // 按实验要求的概率随机生成4种敌机
                    if (random < 0.5) {
                        // 50%概率：普通敌机
                        enemyAircrafts.add(mobEnemyFactory.createEnemy());
                    } else if (random < 0.8) {
                            // 30%概率：精英敌机
                            enemyAircrafts.add(eliteEnemyFactory.createEnemy());
                        } else if (random < 0.95) {
                            // 15%概率：精锐敌机
                            enemyAircrafts.add(elitePlusEnemyFactory.createEnemy());
                        } else {
                            // 5%概率：Boss敌机
                            enemyAircrafts.add(bossEnemyFactory.createEnemy());
                        }
                    }

                    // ========== 敌机射击逻辑（带间隔控制，解决连射） ==========
                    for (AbstractAircraft enemy : enemyAircrafts) {
                        // 只有达到射击间隔，才发射子弹
                        if (enemy.canShoot()) {
                            // 【关键修复2：和父类返回值一致，用BaseBullet接收】
                            List<BaseBullet> newBullets = enemy.shoot();
                            enemyBullets.addAll(newBullets);
                        }
                    }


                    // 飞机发射子弹
                    shootAction();
                    // 子弹移动
                    bulletsMoveAction();
                    // 飞机移动
                    aircraftsMoveAction();
                    // 撞击检测
                    crashCheckAction();
                    // 后处理
                    postProcessAction();
                    // 重绘界面
                    repaint();
                    // 游戏结束检查
                    checkResultAction();
                }
            }

            ;
            // 以固定延迟时间进行执行：本次任务执行完成后，延迟 timeInterval 再执行下一次
        timer.schedule(task,0,timeInterval);

        }


    //***********************
    //      Action 各部分
    //***********************

    private void shootAction() {
        shootCounter++;
        if (shootCounter >= shootCycle) {
            shootCounter = 0;
            //英雄机射击
            heroBullets.addAll(heroAircraft.shoot());
            // 新增：遍历所有敌机，精英敌机按周期射击
            // ========== 敌机射击逻辑（必须加，否则敌机永远不会射击） ==========
            for (AbstractAircraft enemy : enemyAircrafts) {
                // 调用敌机的shoot()方法，获取生成的子弹
                List<BaseBullet> enemyBulletsFromShoot = enemy.shoot();
                // 把生成的子弹加入敌机子弹集合，否则不会渲染
                enemyBullets.addAll(enemyBulletsFromShoot);
            }

        }
    }

    private void bulletsMoveAction() {
        for (BaseBullet bullet : heroBullets) {
            bullet.forward();
        }
        for (BaseBullet bullet : enemyBullets) {
            bullet.forward();
        }
    }

    private void aircraftsMoveAction() {
        for (AbstractAircraft enemyAircraft : enemyAircrafts) {
            enemyAircraft.forward();
        }
    }


    /**
     * 碰撞检测：
     * 1. 敌机攻击英雄
     * 2. 英雄攻击/撞击敌机
     * 3. 英雄获得补给
     */
    private void crashCheckAction() {
        // TODO 敌机子弹攻击英雄机
// ========== 敌机子弹击中英雄机的碰撞扣血逻辑 ==========
        Iterator<BaseBullet> enemyBulletIterator = enemyBullets.iterator();
        while (enemyBulletIterator.hasNext()) {
            BaseBullet enemyBullet = enemyBulletIterator.next();
            // 碰撞判定：敌机子弹和英雄机发生碰撞
            if (enemyBullet.crash(heroAircraft)) {
                // 调试日志：运行看控制台有没有打印，确认碰撞触发
                System.out.println("敌机子弹击中英雄机！子弹伤害：" + enemyBullet.getPower());

                // 1. 子弹击中后消失，从集合移除（避免重复扣血）
                enemyBullet.vanish();
                enemyBulletIterator.remove();

                // 2. 英雄机扣血
                heroAircraft.decreaseHp(enemyBullet.getPower());

                // 3. 控制台打印血量，确认扣血生效
                System.out.println("英雄机当前血量：" + heroAircraft.getHp() + "/" + heroAircraft.getMaxHp());

                // 4. 英雄机血量归0，触发游戏结束
                if (heroAircraft.notValid()) {
                    gameOverFlag = true;
                    System.out.println("英雄机被击毁！游戏结束");
                }
            }
        }

        // ========== 1. 英雄子弹攻击敌机 核心逻辑 ==========
        for (BaseBullet bullet : heroBullets) {
            if (bullet.notValid()) {
                continue;
            }
            for (AbstractAircraft enemyAircraft : enemyAircrafts) {
                if (enemyAircraft.notValid()) {
                    // 已被其他子弹击毁的敌机，不再检测
                    continue;
                }
                if (enemyAircraft.crash(bullet)) {
                    // 播放子弹击中音效
                    AudioManager.getInstance().playSoundEffect(AudioManager.BULLET_HIT);
                    // 敌机撞击到英雄机子弹，扣血
                    enemyAircraft.decreaseHp(bullet.getPower());

                    bullet.vanish();

                    // 敌机被击毁，触发加分+道具掉落
                    if (enemyAircraft.notValid()) {
                        // ========== 不同敌机对应不同加分 ==========
                        int addScore = 10;
                        if (enemyAircraft instanceof EliteEnemy) {
                            addScore = 10;
                        } else if (enemyAircraft instanceof ElitePlusEnemy) {
                            addScore = 30;
                        } else if (enemyAircraft instanceof BossEnemy) {
                            addScore = 100;
                        } else if (enemyAircraft instanceof SuperBoss) {
                            addScore = 300;
                        }
                        score += addScore;

                        // ========== 【核心】按敌机类型实现道具掉落规则 ==========
                        // 道具基础属性
                        int propX = enemyAircraft.getLocationX(); // 直接用敌机中心点坐标，适配你的逻辑
                        int propY = enemyAircraft.getLocationY();
                        int speedX = 0;
                        int speedY = 6;
                        double random = Math.random();
                        if (enemyAircraft.isDropProp()) {
                            // 1. 精英敌机 EliteEnemy：30%概率，掉落3种道具（hp、fire、firePlus）
                            if (enemyAircraft instanceof EliteEnemy) {
                                if (random < 0.3) {
                                    System.out.println("道具掉落触发！生成位置：(" + propX + "," + propY + ")");
                                    String propType;
                                    double propRandom = Math.random();
                                    if (propRandom < 0.33) {
                                        propType = "hp";
                                    } else if (propRandom < 0.66) {
                                        propType = "fire";
                                    } else {
                                        propType = "firePlus";
                                    }
                                    BaseProp prop = PropSimpleFactory.createProp(propType, propX, propY, speedX, speedY);
                                    if (prop != null) {
                                        props.add(prop);
                                    }
                                }
                            }
                        }
                        // 2. 精锐敌机 ElitePlusEnemy：60%概率，掉落4种道具（不含冰冻）
                        else if (enemyAircraft instanceof ElitePlusEnemy) {
                            if (random < 0.6) {
                                String propType;
                                double propRandom = Math.random();
                                if (propRandom < 0.25) {
                                    propType = "hp";
                                } else if (propRandom < 0.5) {
                                    propType = "fire";
                                } else if (propRandom < 0.75) {
                                    propType = "firePlus";
                                } else {
                                    propType = "bomb";
                                }
                                BaseProp prop = PropSimpleFactory.createProp(propType, propX, propY, speedX, speedY);
                                if (prop != null) {
                                    props.add(prop);
                                }
                            }
                        }

                        // 3. 王牌敌机 BossEnemy：100%概率，掉落全部5种道具
                        else if (enemyAircraft instanceof BossEnemy) {
                            String propType;
                            double propRandom = Math.random();
                            if (propRandom < 0.2) {
                                propType = "hp";
                            } else if (propRandom < 0.4) {
                                propType = "fire";
                            } else if (propRandom < 0.6) {
                                propType = "firePlus";
                            } else if (propRandom < 0.8) {
                                propType = "bomb";
                            } else {
                                propType = "freeze";
                            }
                            BaseProp prop = PropSimpleFactory.createProp(propType, propX, propY, speedX, speedY);
                            if (prop != null) {
                                props.add(prop);
                            }
                        }

                        // 4. 超级Boss SuperBoss：100%掉落3个道具，全5种随机
                        else if (enemyAircraft instanceof SuperBoss) {
                            System.out.println("超级Boss被击毁，开始生成道具");
                            AudioManager.getInstance().playBGM(AudioManager.BGM);
                            for (int i = 0; i < 3; i++) {
                                // 3个道具横向错开，避免重叠
                                int offsetX = (i - 1) * 40;
                                String propType;
                                double propRandom = Math.random();
                                if (propRandom < 0.2) {
                                    propType = "hp";
                                } else if (propRandom < 0.4) {
                                    propType = "fire";
                                } else if (propRandom < 0.6) {
                                    propType = "firePlus";
                                } else if (propRandom < 0.8) {
                                    propType = "bomb";
                                } else {
                                    propType = "freeze";
                                }
                                BaseProp prop = PropSimpleFactory.createProp(propType, propX + offsetX, propY, speedX, speedY);
                                if (prop != null) {
                                    props.add(prop);
                                    System.out.println("超级Boss掉落道具：" + prop.getClass().getSimpleName());
                                }
                            }
                        }

                        // 【必须放在最后】敌机消失，必须在道具生成之后执行
                        enemyAircraft.vanish();
                    }
                }

                // ========== 英雄机与敌机相撞，均损毁 ==========
                if (enemyAircraft.isValid() && (enemyAircraft.crash(heroAircraft) || heroAircraft.crash(enemyAircraft))) {
                    enemyAircraft.vanish();
                    heroAircraft.decreaseHp(Integer.MAX_VALUE);
                }
            }
        }

        // ========== 2. 道具拾取+生效核心逻辑 ==========
        Iterator<BaseProp> propIterator = props.iterator();
        while (propIterator.hasNext()) {
            BaseProp prop = propIterator.next();
            if (prop.crash(heroAircraft)) {
                System.out.println("成功拾取道具：" + prop.getClass().getSimpleName());
                if (prop instanceof BombProp) {
                    // 炸弹道具的处理（保留你原来的代码不变）
                    AudioManager.getInstance().playSoundEffect(AudioManager.BOMB_EXPLOSION);
                    BombProp bombProp = (BombProp) prop;
                    for (AbstractAircraft enemy : enemyAircrafts) {
                        bombProp.addObserver(enemy);
                    }
                    for (BaseBullet bullet : enemyBullets) {
                        bombProp.addObserver(bullet);
                    }
                    bombProp.notifyBomb();
                }
                // 👇【新增】冰冻道具的处理，和炸弹逻辑完全对称
                else if (prop instanceof FreezeProp) {
                    AudioManager.getInstance().playSoundEffect(AudioManager.GET_SUPPLY);
                    FreezeProp freezeProp = (FreezeProp) prop;
                    // 把所有敌机注册为观察者
                    for (AbstractAircraft enemy : enemyAircrafts) {
                        freezeProp.addObserver(enemy);
                    }
                    // 把所有敌机子弹注册为观察者
                    for (BaseBullet bullet : enemyBullets) {
                        freezeProp.addObserver(bullet);
                    }
                    // 触发冰冻通知
                    freezeProp.notifyFreeze();
                }
                else {
                    // 普通道具（火力/加血）的音效
                    AudioManager.getInstance().playSoundEffect(AudioManager.GET_SUPPLY);
                }

                prop.vanish();
                propIterator.remove();

                // 👇【修改点】把FreezeProp也加入判断，避免重复调用active
                if (!(prop instanceof BombProp || prop instanceof FreezeProp)) {
                    prop.active(heroAircraft);
                }
            }
        }



    }


    /**
     * 后处理：
     * 1. 删除无效的子弹
     * 2. 删除无效的敌机
     * 3. 删除无效的道具
     */
    private void postProcessAction() {
        // 替换为lambda表达式，完全兼容Java 11，解决方法引用报错
        enemyBullets.removeIf(obj -> obj.notValid());
        heroBullets.removeIf(obj -> obj.notValid());
        enemyAircrafts.removeIf(obj -> obj.notValid());
        // 【补全】删除无效的道具，解决内存残留问题
        props.removeIf(obj -> obj.notValid());
    }


    /**
     * 检查游戏是否结束，若结束：关闭线程池
     */
    private void checkResultAction() {
        // 游戏结束检查英雄机是否存活
        if (heroAircraft.getHp() <= 0) {
            // 停止BGM，播放游戏结束音效
            AudioManager.getInstance().stopBGM();
            AudioManager.getInstance().playSoundEffect(AudioManager.GAME_OVER);
            timer.cancel(); // 取消定时器并终止所有调度任务
            gameOverFlag = true;
            System.out.println("Game Over!");
            // ========== 1. 新增玩家姓名输入逻辑 ==========
            // 弹出输入框，让玩家输入姓名（支持取消/空输入的容错处理）
            String userName = JOptionPane.showInputDialog(
                    this,
                    "游戏结束！请输入你的姓名：",
                    "保存成绩",
                    JOptionPane.PLAIN_MESSAGE
            );

            // 处理用户点击取消、或输入为空的情况，设置默认玩家名
            if (userName == null || userName.trim().isEmpty()) {
                userName = "匿名玩家"; // 可根据需求修改为"testUser"等默认名
            }

            // ========== 2. 保存分数记录（绑定当前难度，实现隔离） ==========
            ScoreRecord record = new ScoreRecord(
                    userName.trim(), // 去除前后空格，避免无效输入
                    score, // 当前游戏得分
                    LocalDateTime.now(), // 当前时间
                    gameDifficulty // 绑定当前游戏难度，确保数据隔离
            );
            scoreDao.addRecord(record);

            // ========== 3. 控制台打印排行榜（满足实验要求） ==========
            scoreDao.printRankingList();

            // ========== 修正后的游戏结束跳转逻辑 ==========
// 1. 创建当前难度的RankPanel实例（只加载对应难度的排行榜）
            RankPanel rankPanel = new RankPanel(gameDifficulty);
// 2. 把排行榜面板加到GameFrame的卡片容器中（必须加这一步！）
            GameFrame.cardPanel.add(rankPanel.getMainPanel(), GameFrame.RANK_PANEL);
// 3. 调用GameFrame的showPanel方法，切换到排行榜界面
            GameFrame.getInstance().showPanel(GameFrame.RANK_PANEL);

        }
    }

            ;

            //***********************
            //      Paint 各部分
            //***********************

            /**
             * 重写 paint方法
             * 通过重复调用paint方法，实现游戏动画
             */
            @Override
            public void paint(Graphics g) {
                super.paint(g);

                // 【关键修改】把固定的BACKGROUND_IMAGE，换成你构造方法里赋值的this.background成员变量
                g.drawImage(this.background, 0, this.backGroundTop - Main.WINDOW_HEIGHT, null);
                g.drawImage(this.background, 0, this.backGroundTop, null);
                this.backGroundTop += 1;
                if (this.backGroundTop == Main.WINDOW_HEIGHT) {
                    this.backGroundTop = 0;
                }

                // 先绘制子弹，后绘制飞机
                // 这样子弹显示在飞机的下层
                paintImageWithPositionRevised(g, enemyBullets);
                paintImageWithPositionRevised(g, heroBullets);
                paintImageWithPositionRevised(g, enemyAircrafts);
                // 绘制道具
                paintImageWithPositionRevised(g, props);

                // Todo: 绘制道具

                g.drawImage(ImageManager.HERO_IMAGE, heroAircraft.getLocationX() - ImageManager.HERO_IMAGE.getWidth() / 2,
                        heroAircraft.getLocationY() - ImageManager.HERO_IMAGE.getHeight() / 2, null);

                //绘制得分和生命值
                paintScoreAndLife(g);

            }

            private void paintImageWithPositionRevised(Graphics g, List<? extends AbstractFlyingObject> objects) {
                if (objects.isEmpty()) {
                    return;
                }

                for (AbstractFlyingObject object : objects) {
                    BufferedImage image = object.getImage();
                    assert image != null : objects.getClass().getName() + " has no image! ";
                    g.drawImage(image, object.getLocationX() - image.getWidth() / 2,
                            object.getLocationY() - image.getHeight() / 2, null);
                }
            }

            private void paintScoreAndLife(Graphics g) {
                int x = 10;
                int y = 25;
                g.setColor(Color.RED);
                g.setFont(new Font("SansSerif", Font.BOLD, 22));
                g.drawString("SCORE: " + this.score, x, y);
                y = y + 20;
                g.drawString("LIFE: " + this.heroAircraft.getHp(), x, y);
            }

        }



