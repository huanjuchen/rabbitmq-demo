package maxlen.consumer;

import api.utils.RabbitMqUtil;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DeliverCallback;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;

/**
 * @author HuanJu
 * @date 2021/10/4 16:35
 */
@Slf4j
public class MaxLenDeadConsumer {

    private static final String NORMAL_EXCHANGE = "max-len-normal-exchange";

    private static final String DEAD_EXCHANGE = "max-len-dead-exchange";

    public static void main(String[] args) throws IOException, TimeoutException {

        Channel channel = RabbitMqUtil.getChannel();

        /*
        定义死信交换机
        并绑定队列
         */
        channel.exchangeDeclare(DEAD_EXCHANGE, BuiltinExchangeType.DIRECT);
        String deadQueue = "max-len-dead-queue";
        channel.queueDeclare(deadQueue, false, false, false, null);
        channel.queueBind(deadQueue, DEAD_EXCHANGE, "max-len-dead");

        /*
        正常队列参数
         */
        Map<String, Object> params = new HashMap<>();
        //死信交换机
        params.put("x-dead-letter-exchange", DEAD_EXCHANGE);
        //死信routing key
        params.put("x-dead-letter-routing-key", "max-len-dead");
        //正常队列长度限制
        params.put("x-max-length", 5);

        //正常交换机
        channel.exchangeDeclare(NORMAL_EXCHANGE, BuiltinExchangeType.DIRECT);
        //定义正常队列并绑定
        String normalQueue = "max-len-normal-queue";
        channel.queueDeclare(normalQueue, false, false, false, params);
        channel.queueBind(normalQueue, NORMAL_EXCHANGE, "max-len-normal");

        log.info("MaxLen死信消费者等待消费...");

        DeliverCallback deliverCallback = (consumerTag, message) -> {

            String msg = new String(message.getBody(), StandardCharsets.UTF_8);
            log.info("MaxLen死信消费者接收的消息：{}", msg);
        };

        channel.basicConsume(deadQueue, true, deliverCallback, consumerTag -> {
        });
    }


}
