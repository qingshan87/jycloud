package com.jycloud.tool.common.util;

/**
 * @author Lqs
 * @date 2025/4/11 17:00
 */
public class SnowflakeIdGenerator {

    // 工作机器 ID（范围：0 ~ 31，5 位）
    private final long workerId;

    // 数据中心 ID（范围：0 ~ 31，5 位）
    private final long datacenterId;

    // 起始时间戳（2010-11-04 01:42:54.657），用于计算时间差
    private final long twepoch = 1288834974657L;

    // 序列号（范围：0 ~ 4095，12 位），用于同一毫秒内生成多个 ID
    private long sequence = 0L;

    // 工作机器 ID 所占的位数（5 位）
    private final long workerIdBits = 5L;

    // 数据中心 ID 所占的位数（5 位）
    private final long datacenterIdBits = 5L;

    // 最大工作机器 ID（31，即 2^5 - 1）
    private final long maxWorkerId = -1L ^ (-1L << workerIdBits);

    // 最大数据中心 ID（31，即 2^5 - 1）
    private final long maxDatacenterId = -1L ^ (-1L << datacenterIdBits);

    // 序列号所占的位数（12 位）
    private final long sequenceBits = 12L;

    // 工作机器 ID 的左移位数（12 位）
    private final long workerIdShift = sequenceBits;

    // 数据中心 ID 的左移位数（12 + 5 = 17 位）
    private final long datacenterIdShift = sequenceBits + workerIdBits;

    // 时间戳的左移位数（12 + 5 + 5 = 22 位）
    private final long timestampLeftShift = sequenceBits + workerIdBits + datacenterIdBits;

    // 序列号掩码（4095，即 2^12 - 1），用于防止序列号溢出
    private final long sequenceMask = -1L ^ (-1L << sequenceBits);

    // 上一次生成 ID 的时间戳
    private long lastTimestamp = -1L;

    /**
     * 构造函数，初始化工作机器 ID 和数据中心 ID
     *
     * @param workerId     工作机器 ID（范围：0 ~ 31）
     * @param datacenterId 数据中心 ID（范围：0 ~ 31）
     */
    public SnowflakeIdGenerator(long workerId, long datacenterId) {
        // 检查工作机器 ID 是否合法
        if (workerId > maxWorkerId || workerId < 0) {
            throw new IllegalArgumentException("worker Id can't be greater than " + maxWorkerId + " or less than 0");
        }
        // 检查数据中心 ID 是否合法
        if (datacenterId > maxDatacenterId || datacenterId < 0) {
            throw new IllegalArgumentException("datacenter Id can't be greater than " + maxDatacenterId + " or less than 0");
        }
        this.workerId = workerId;
        this.datacenterId = datacenterId;
    }

    /**
     * 获取下一个全局唯一 ID
     *
     * @return 全局唯一 ID
     */
    public synchronized long nextId() {
        // 获取当前时间戳（毫秒级）
        long timestamp = timeGen();

        // 如果当前时间小于上一次生成 ID 的时间戳，说明系统时钟回退了
        if (timestamp < lastTimestamp) {
            throw new RuntimeException("Clock moved backwards. Refusing to generate id");
        }

        // 如果当前时间与上一次生成 ID 的时间戳相同，则增加序列号
        if (lastTimestamp == timestamp) {
            // 序列号自增，并通过掩码确保不会溢出
            sequence = (sequence + 1) & sequenceMask;

            // 如果序列号溢出了（达到了最大值 4095），等待下一毫秒
            if (sequence == 0) {
                timestamp = tilNextMillis(lastTimestamp);
            }
        } else {
            // 如果时间戳不同，则重置序列号
            sequence = 0L;
        }

        // 更新上一次生成 ID 的时间戳
        lastTimestamp = timestamp;

        // 组合 64 位 ID
        return ((timestamp - twepoch) << timestampLeftShift) |  // 时间戳部分
                (datacenterId << datacenterIdShift) |            // 数据中心 ID 部分
                (workerId << workerIdShift) |                    // 工作机器 ID 部分
                sequence;                                        // 序列号部分
    }

    /**
     * 等待直到下一毫秒
     *
     * @param lastTimestamp 上一次生成 ID 的时间戳
     * @return 当前时间戳
     */
    private long tilNextMillis(long lastTimestamp) {
        long timestamp = timeGen();
        // 循环等待，直到当前时间大于上一次生成 ID 的时间戳
        while (timestamp <= lastTimestamp) {
            timestamp = timeGen();
        }
        return timestamp;
    }

    /**
     * 获取当前时间戳（毫秒级）
     *
     * @return 当前时间戳
     */
    private long timeGen() {
        return System.currentTimeMillis();
    }


    /**
     * 测试方法，生成 10 个唯一 ID
     */
    public static void main(String[] args) {
        // 初始化 Snowflake ID 生成器（工作机器 ID 和数据中心 ID 分别为 1 和 1）
        SnowflakeIdGenerator idGenerator = new SnowflakeIdGenerator(1, 1);
        SnowflakeIdGenerator idGenerator2 = new SnowflakeIdGenerator(2, 1);

        // 生成并打印 10 个唯一 ID
        for (int i = 0; i < 10; i++) {
            System.out.println("idGenerator:" + idGenerator.nextId());

            System.out.println("idGenerator2:" + idGenerator2.nextId());
        }
    }

}
