package ack.consumer;


import api.utils.RabbitMqUtil;
import api.utils.SleepUtils;
import com.rabbitmq.client.CancelCallback;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DeliverCallback;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeoutException;


/**
 * @author HuanJu
 * @date 2021/9/27 22:47
 */
@Slf4j
public class AckConsumer01 {

    private static final String TASK_QUEUE_NAME = "ack_queue";

    public static void main(String[] args) throws IOException, TimeoutException {
        Channel channel = RabbitMqUtil.getChannel();
        log.info("C1等待接收消息，处理时间较短");
        DeliverCallback deliverCallback = (consumerTag, message) -> {
            String msg = new String(message.getBody(), StandardCharsets.UTF_8);
            SleepUtils.sleep(1L);
            log.info("C1接收的消息：{}", msg);

            /*
            1.消息标记 tag
            2.是否批量应答未答应消息
             */
            channel.basicAck(message.getEnvelope().getDeliveryTag(), false);
        };

        CancelCallback cancelCallback = consumerTag -> log.error("{}消费者取消消费接口回调逻辑", consumerTag);
        //采用手动应答
        boolean autoAck = false;
        channel.basicConsume(TASK_QUEUE_NAME, autoAck, deliverCallback, cancelCallback);


    }

}
