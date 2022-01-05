package com.zealotpz.quartz.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;

/**
 * description: 雪花算法
 *
 * @author: zealotpz
 * create: 2022-01-04 16:32
 **/

@Component
public class SnowFlakeUtil {
    /**
     * 起始时间戳：2022-01-01 00:00:00
     */
    private final static long START_TIMESTAMP = 1640966400000L;

    /**
     * 序列号占用的位数，12可以每毫秒生成4096个，一般业务根本不需要用到这么大的并发，所以缩短到10位，
     * 当然也可以缩减数据中心和机器id的位数
     */
    private final static long SEQUENCE_BIT = 10;
    /**
     * 时钟回拨扩展次数，最大承受三次时钟回拨
     */
    private final static long BACK_EXTENSION_BIT = 2;
    /**
     * 机器标识占用的位数
     */
    private final static long MACHINE_BIT = 5;
    /**
     * 数据中心占用的位数
     */
    private final static long DATACENTER_BIT = 5;
    /**
     * 每一部分的最大值：先进行左移运算，在进行非运算取反
     * <p>
     * 用位运算计算出最大支持的数据中心数量：31
     */
    private final static long MAX_DATACENTER_NUM = ~(-1L << DATACENTER_BIT);

    /**
     * 用位运算计算出最大支持的机器数量：31
     */
    private final static long MAX_MACHINE_NUM = ~(-1L << MACHINE_BIT);

    /**
     * 用位运算计算出12位能存储的最大正整数：4095
     */
    private final static long MAX_SEQUENCE = ~(-1L << SEQUENCE_BIT);
    /**
     * 序列号较回拨次数偏移量
     */
    private final static long SEQUENCE_LEFT = BACK_EXTENSION_BIT;
    /**
     * 机器标志较序列号的偏移量
     */
    private final static long MACHINE_LEFT = SEQUENCE_BIT + BACK_EXTENSION_BIT;

    /**
     * 数据中心较机器标志的偏移量
     */
    private final static long DATACENTER_LEFT = MACHINE_LEFT + MACHINE_BIT;

    /**
     * 时间戳较数据中心的偏移量
     */
    private final static long TIMESTAMP_LEFT = DATACENTER_LEFT + DATACENTER_BIT;
    /**
     * 时钟回拨最大值 3 毫秒，不建议大于 5 毫秒
     */
    private final static long MAX_BACKWARD_MS = 3;
    /**
     * 最大次数
     */
    private final static long MAX_BACKWARD_COUNT = ~(-1L << BACK_EXTENSION_BIT);
    /**
     * 序列号
     */
    private static long sequence = 0L;
    /**
     * 时钟回拨次数
     */
    private static long backwardCount = 0L;
    /**
     * 上一次时间戳
     */
    private static long lastStamp = -1L;

    /**
     * 数据中心
     */
    private static long datacenterId;

    @Value("${snowFlake.dataCenter.id:1}")
    public void setDataCenterId(long snowFlakeDataCenterId) {
        datacenterId = snowFlakeDataCenterId;
    }

    /**
     * 机器标识
     */
    private static long machineId;

    @Value("${snowFlake.machine.id:1}")
    public void setMachineId(long snowFlakeMachineId) {
        machineId = snowFlakeMachineId;
    }

//    /**
//     * 数据中心
//     */
//    private long datacenterId;
//    /**
//     * 机器标识
//     */
//    private long machineId;


    /**
     * 此处无参构造私有，同时没有给出有参构造，在于避免以下两点问题：
     * 1、私有化避免了通过new的方式进行调用，主要是解决了在for循环中通过new的方式调用产生的id不一定唯一问题问题，
     * 因为用于记录上一次时间戳的lastStamp永远无法得到比对；
     * 2、没有给出有参构造在第一点的基础上考虑了一套分布式系统产生的唯一序列号应该是基于相同的参数
     */
    private SnowFlakeUtil() {
    }

    /**
     * 构造方法初始化数据中心 ID 和机器 ID
     */
//    public SnowFlakeUtil(long machineId, long datacenterId) {
//        if (machineId > MAX_MACHINE_NUM || machineId < 0) {
//            throw new IllegalArgumentException(String.format("worker Id can't be greater than % d or less than 0", MAX_MACHINE_NUM));
//        }
//        if (datacenterId > MAX_DATACENTER_NUM || datacenterId < 0) {
//            throw new IllegalArgumentException(String.format("datacenter Id can't be greater than % d or less than 0", MAX_DATACENTER_NUM));
//        }
//        this.machineId = machineId;
//        this.datacenterId = datacenterId;
//    }

    /**
     * 获取下一个 id
     *
     * @return next id
     */
    public static synchronized long nextId() {
        // 当前时间戳
        long currStamp = getNewStamp();
        // 当前时间戳小于上次时间戳，出现时钟回拨
//        if (currStamp < lastStamp) {
//            throw new RuntimeException("Clock moved backwards.  Refusing to generate id");
//        }
        //TODO 时钟回拨问题
        // 1. 禁止时钟同步
        // 2. 自旋等待时钟追回
        // 3. 增加标志位,出现时钟回拨+1,达到最大值后清零

        // ↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓
        // 偏移量
        long offset = lastStamp - currStamp;
        // 可接受等待的回拨
        if (offset <= MAX_BACKWARD_MS) {
            // 休眠等待
            LockSupport.parkNanos(TimeUnit.MICROSECONDS.toNanos(offset));
            // 重新获取当前值
            currStamp = getNewStamp();
            // 如果仍然小于上次时间戳，可以直接抛异常或者采用扩展字段 extension
            if (currStamp < lastStamp) {
                if (backwardCount < MAX_BACKWARD_COUNT) {
                    backwardCount++;
                    return bitOption(currStamp);
                } else {
                    // 三次回拨后直接抛出异常，后续进行人为干预，处理好服务器时间
                    throw new RuntimeException("Clock moved backwards.  Refusing to generate id");
                }
            }
        } else {
            // 直接标志位值+1
            if (backwardCount < MAX_BACKWARD_COUNT) {
                backwardCount++;
                return bitOption(currStamp);
            } else {
                // 三次回拨后直接抛出异常，后续进行人为干预，处理好服务器时间
                throw new RuntimeException("Clock moved backwards.  Refusing to generate id");
            }
        }
        // ↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑

        // 同一毫秒内
        if (currStamp == lastStamp) {
            sequence = (sequence + 1) & MAX_SEQUENCE;
            // 同一毫秒的序列数已经达到最大
            if (sequence == 0L) {
                // 获取下一时间的时间戳并赋值给当前时间戳
                currStamp = getNextMill();
            }
        } else {
            // 不同毫秒内，重置序列为0
            sequence = 0L;
        }
        lastStamp = currStamp;
        return bitOption(currStamp);
    }

    /**
     * 位运算拼接
     * 生成id的过程，就是把每一种标识（时间、机器、序列号）移到对应位置，然后相加。
     * deltaTime 向左移22位（IDC-bit+机器bit+序列号bit）。
     * dataCenterId 向左移17位（机器bit+序列号bit）。
     * machineId 向左移10位（序列号bit）。
     * sequence 序列号 。
     * backwardCount 时钟回拨次数不用移
     * 中间的 | 以运算规律就相当于+求和（1 | 1 = 1，1 | 0 = 1，0 | 1 = 1，0 | 0 = 0）。
     */
    private static long bitOption(long currStamp) {
        // 时间戳 | 数据中心 | 机器码 | 序列号 | 时钟回拨次数
        return (currStamp - START_TIMESTAMP) << TIMESTAMP_LEFT | datacenterId << DATACENTER_LEFT | machineId << MACHINE_LEFT | sequence << SEQUENCE_LEFT | backwardCount;
    }

    /**
     * 获取下一个时间戳
     *
     * @return 时间戳
     */
    private static long getNextMill() {
        long mill = getNewStamp();
        while (mill <= lastStamp) {
            mill = getNewStamp();
        }
        return mill;
    }

    private static long getNewStamp() {
        return System.currentTimeMillis();
    }


}
