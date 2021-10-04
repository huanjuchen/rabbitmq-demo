package sendack.consumer;

import api.utils.RabbitMqUtil;
import com.rabbitmq.client.CancelCallback;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DeliverCallback;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeoutException;

/**
 * @author HuanJu
 * @date 2021/9/28 21:24
 */
@Slf4j
public class SendAckConsumer {

    private static final String SEND_ACK_QUEUE = "send_ack_queue";

    public static void main(String[] args) throws IOException, TimeoutException {

        Channel channel = RabbitMqUtil.getChannel();

        log.info("等待接收消息...");

        DeliverCallback deliverCallback = (consumerTag, message) -> {

            String msg = new String(message.getBody(), StandardCharsets.UTF_8);

            log.info("接收到的消息: {}", msg);
            //手动应答
            channel.basicAck(message.getEnvelope().getDeliveryTag(), false);

        };

        CancelCallback cancelCallback = consumerTag -> log.error("{}消费取消...", consumerTag);

        channel.basicConsume(SEND_ACK_QUEUE, false, deliverCallback, cancelCallback);

    }


}
