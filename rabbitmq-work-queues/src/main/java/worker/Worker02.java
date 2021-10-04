package worker;

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
 * @date 2021/9/25 20:29
 */
public class Worker02 {

    private static final Logger log = LoggerFactory.getLogger(Worker02.class);

    private static final String QUEUE_NAME = "hello1";

    public static void main(String[] args) throws IOException, TimeoutException {
        Channel channel = RabbitMqUtil.getChannel();
        DeliverCallback deliverCallback = ((consumerTag, message) -> {
            String msg = new String(message.getBody(), StandardCharsets.UTF_8);
            log.info("接到的消息：{}", msg);
        });
        CancelCallback cancelCallback = consumerTag -> log.info("{} 消费者取消消费端口回调逻辑", consumerTag);
        channel.basicConsume(QUEUE_NAME, true, deliverCallback, cancelCallback);
    }

}
