package consumer;

import api.utils.RabbitMqUtil;
import com.rabbitmq.client.CancelCallback;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DeliverCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeoutException;

/**
 * @author HuanJu
 * @date 2021/9/25 19:18
 */
public class HelloWorldConsumer {

    private final static Logger log = LoggerFactory.getLogger(HelloWorldConsumer.class);

    public static void main(String[] args) throws IOException, TimeoutException {

        Channel channel = RabbitMqUtil.getChannel();

        log.info("等待消息...");
        /*
         推送的消息 消费成功接口回调
         */
        DeliverCallback deliverCallback = ((consumerTag, message) -> {
            String msg = new String(message.getBody(), StandardCharsets.UTF_8);
            log.info("接收的消息：{}", msg);
        });

        /*
        取消消费的接口回调 如在消费时队列被删除
         */
        CancelCallback cancelCallback = consumerTag -> log.info("消息消费被中断...");


        /*
        消费者消费消息
        1.队列
        2.消费成功是否自动应答
        3.消费者消费成功回调
        4.消费者未消费成功回调
         */
        channel.basicConsume("hello", true, deliverCallback, cancelCallback);
    }

}
