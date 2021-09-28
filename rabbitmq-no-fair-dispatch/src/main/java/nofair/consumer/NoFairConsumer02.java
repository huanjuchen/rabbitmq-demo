package nofair.consumer;

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
 * @date 2021/9/27 23:43
 */
@Slf4j
public class NoFairConsumer02 {
    private static final String NO_FAIR_QUEUES = "no_fair_queues";

    public static void main(String[] args) throws IOException, TimeoutException {
        Channel channel = RabbitMqUtil.getChannel();
        log.info("消费者02消费等待");
        DeliverCallback deliverCallback = (consumerTag, message) -> {
            SleepUtils.sleep(10L);
            String msg = new String(message.getBody(), StandardCharsets.UTF_8);
            log.info("02接收消息：{}", msg);
            log.info("consumeTag: {}", consumerTag);
            //手动应答
            channel.basicAck(message.getEnvelope().getDeliveryTag(), false);
        };

        CancelCallback cancelCallback = consumerTag -> log.warn("02消费取消：{}", consumerTag);

        //设置预取值
        int preFetchCount = 1;
        channel.basicQos(preFetchCount);

        channel.basicConsume(NO_FAIR_QUEUES, false, deliverCallback, cancelCallback);
    }
}
