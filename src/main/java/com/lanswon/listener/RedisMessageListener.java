package com.lanswon.listener;

import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class RedisMessageListener implements MessageListener {
    private Logger logger = LoggerFactory.getLogger(RedisMessageListener.class);
    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Override
    public void onMessage(Message message, byte[] pattern) {
        // TODO Auto-generated method stub
        byte[] body = message.getBody();// 请使用valueSerializer
        byte[] channel = message.getChannel();
        // 请参考配置文件，本例中key，value的序列化方式均为string。
        // 其中key必须为stringSerializer。和redisTemplate.convertAndSend对应
        String msgContent = (String) redisTemplate.getValueSerializer().deserialize(body);
        String topic = (String) redisTemplate.getStringSerializer().deserialize(channel);
        logger.info("redisMessage...msgContent:" + msgContent + "...topic:" + topic);
    }

    @PostConstruct
    public void messageListener() {
        System.out.println("redis缓存发布");
        /*new Thread(new Runnable() {
			@Override
			public void run() {
				while (true) {
//					System.out.println(rightPop("messageList"));
					try {
						Thread.sleep(5000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}, "消息监听任务线程").start();*/
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
        logger.info("rightPop");
        Object result = (String) redisTemplate.opsForList().rightPop(key);
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