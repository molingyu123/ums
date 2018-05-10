package com.lanswon.util;

import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.RedisTemplate;

public class RedisUtils {
    private RedisTemplate<String, Object> redisTemplate;

    public RedisUtils() {

    }

    public RedisUtils(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    /**
     * redis发布消息
     *
     * @param channel
     * @param message
     */
    public void sendMessage(String channel, String message) {
        redisTemplate.convertAndSend(channel, message);
    }

    /**
     * 向指定的列表左边插入数据
     *
     * @param key
     * @param value
     * @return
     */
    public void leftPush(String key, String value) {
        redisTemplate.opsForList().leftPush(key, value);
    }

    /**
     * 弹出指定列表右边的数据(如果没有数据,在指定的时间内等待)
     *
     * @param key
     * @param timeout
     * @param unit
     * @return
     */
    public String rightPop(String key) {
        Object result = (String) redisTemplate.opsForList().rightPop(key);
        System.out.println(result);
        if (result == null) {
            return "null";
        } else {
            return (String) result;
        }
    }

    /**
     * 弹出指定列表右边,并向指定列表的左边插入(弹出列表如果没有元素,等待指定的时间)
     *
     * @param sourceKey
     * @param destinationKey
     * @param timeout
     * @param unit
     * @return
     */
    public String rightPopAndLeftPush(String sourceKey, String destinationKey, long timeout, TimeUnit unit) {
        return (String) redisTemplate.opsForList().rightPopAndLeftPush(sourceKey, destinationKey, timeout, unit);
    }
}