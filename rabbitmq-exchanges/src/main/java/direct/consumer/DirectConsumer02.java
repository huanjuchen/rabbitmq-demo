package direct.consumer;

import api.utils.RabbitMqUtil;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DeliverCallback;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeoutException;

/**
 * @author HuanJu
 * @date 2021/10/2 23:53
 */
@Slf4j
public class DirectConsumer02 {

    public static final String ROUTING_KEY_INFO = "info";

    public static final String ROUTING_KEY_WARNING = "warning";

    private static final String EXCHANGE_NAME = "direct_logs";


    public static void main(String[] args) throws IOException, TimeoutException {
        Channel channel = RabbitMqUtil.getChannel();

        channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.DIRECT);

        String queueName = "console";

        channel.queueDeclare(queueName, true, false, false, null);

        channel.queueBind(queueName, EXCHANGE_NAME, ROUTING_KEY_INFO);
        channel.queueBind(queueName, EXCHANGE_NAME, ROUTING_KEY_WARNING);

        log.info("等待接收消息到控制台。。。");

        DeliverCallback deliverCallback = (consumerTag, message) -> {

            String msg = new String(message.getBody(), StandardCharsets.UTF_8);

            log.info("接收绑定键：{}，消息：{}",message.getEnvelope().getRoutingKey(),msg);

            channel.basicAck(message.getEnvelope().getDeliveryTag(),false);
        };

        int preFetchCount = 1;

        channel.basicQos(1);

        channel.basicConsume(queueName,false,deliverCallback,consumerTag -> {});

    }


}
