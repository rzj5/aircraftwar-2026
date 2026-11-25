package edu.hitsz.aircraft;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 英雄机单元测试类，对应指导书的CalculatorTest
 * 至少覆盖3个方法，包含父类的方法
 */
class HeroAircraftTest {
    private HeroAircraft heroAircraft;

    // 每个测试方法执行前，初始化英雄机
    @BeforeEach
    void setUp() {
        System.out.println("=== 测试方法执行前，初始化英雄机 ===");
        // 获取英雄机单例，重置血量、位置
        heroAircraft = HeroAircraft.getInstance(400, 500, 0, 0, 100);
        heroAircraft.setHp(100); // 重置血量为100，方便测试
    }

    // 每个测试方法执行后，清空英雄机
    @AfterEach
    void tearDown() {
        System.out.println("=== 测试方法执行后，清理资源 ===\n");
        heroAircraft = null;
    }

    /**
     * 测试1：decreaseHp() 扣血方法（父类AbstractAircraft的方法）
     * 对应指导书的add/sub测试
     */
    @Test
    @DisplayName("测试英雄机扣血逻辑")
    void decreaseHp() {
        System.out.println("执行测试：扣血逻辑");
        // 初始血量100，扣30，预期剩余70
        heroAircraft.decreaseHp(30);
        assertEquals(70, heroAircraft.getHp());

        // 再扣50，预期剩余20
        heroAircraft.decreaseHp(50);
        assertEquals(20, heroAircraft.getHp());

        // 扣超过血量的数值，预期血量0，飞机失效
        heroAircraft.decreaseHp(30);
        assertEquals(0, heroAircraft.getHp());
        assertTrue(heroAircraft.notValid());
    }

    /**
     * 测试2：shoot() 射击方法（英雄机的核心方法）
     * 对应指导书的mul/div测试
     */
    @Test
    @DisplayName("测试英雄机射击方法")
    void shoot() {
        System.out.println("执行测试：射击逻辑");
        // 射击方法应该返回非空的子弹列表
        assertNotNull(heroAircraft.shoot());
        // 普通射击，预期子弹数量不为0
        assertTrue(heroAircraft.shoot().size() > 0);
    }

    /**
     * 测试3：forward() 移动方法（父类AbstractAircraft的方法）
     */
    @Test
    @DisplayName("测试英雄机坐标设置与获取（父类方法）")
    void locationTest() {
        System.out.println("执行测试：英雄机坐标设置与获取");
        // 测试X坐标的set/get（父类AbstractAircraft的方法）
        heroAircraft.setLocationX(450);
        assertEquals(450, heroAircraft.getLocationX());

        // 测试Y坐标的set/get
        heroAircraft.setLocationY(550);
        assertEquals(550, heroAircraft.getLocationY());
    }
}
