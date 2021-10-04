package ttl.consumer;

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
 * @date 2021/10/4 16:16
 */
@Slf4j
public class TtlDeadConsumer {

    /**
     * 死信交换机
     */
    private final static String DEAD_EXCHANGE = "ttl-dead-exchange";

    /**
     * 正常交换机
     */
    private final static String NORMAL_EXCHANGE = "ttl-normal-exchange";


    public static void main(String[] args) throws IOException, TimeoutException {
        Channel channel = RabbitMqUtil.getChannel();
        //定义正常交换机
        channel.exchangeDeclare(NORMAL_EXCHANGE, BuiltinExchangeType.DIRECT);
        //定义死信交换机
        channel.exchangeDeclare(DEAD_EXCHANGE, BuiltinExchangeType.DIRECT);
        /*
        定义死信队列 并绑定key 为 ttl
         */
        String deadQueue = "ttl-dead-queue";
        channel.queueDeclare(deadQueue, false, false, false, null);
        channel.queueBind(deadQueue, DEAD_EXCHANGE, "ttl-dead");
        /*
        正常队列参数
         */
        Map<String, Object> params = new HashMap<>();
        //正常队列设置死信交换机
        params.put("x-dead-letter-exchange", DEAD_EXCHANGE);
        //正常队列设置死信队列routing-key
        params.put("x-dead-letter-routing-key", "ttl-dead");

        /*
        定义正常队列
         */
        String normalQueue = "normal-queue";
        channel.queueDeclare(normalQueue, false, false, false, params);
        channel.queueBind(normalQueue,NORMAL_EXCHANGE,"ttl-normal");
        log.info("Dead消费者等待消费...");
        //定义死信队列消费callback

        DeliverCallback deliverCallback = (consumerTag, message) -> {
            String msg = new String(message.getBody(), StandardCharsets.UTF_8);
            log.info("Dead消费者接收的信息：{}", msg);
        };

        channel.basicConsume(deadQueue,true,deliverCallback,consumerTag -> {});
    }

}
