package reject.consumer;

import api.utils.RabbitMqUtil;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DeliverCallback;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeoutException;

/**
 * @author HuanJu
 * @date 2021/10/4 22:25
 */
@Slf4j
public class RejectNormalConsumer {

    private static final String NORMAL_EXCHANGE = "reject-normal-exchange";

    private static final String DEAD_EXCHANGE = "reject-dead-exchange";

    public static void main(String[] args) throws IOException, TimeoutException {

        Channel channel = RabbitMqUtil.getChannel();

        /*
        定义死信交换机
        并绑定队列
         */
        channel.exchangeDeclare(DEAD_EXCHANGE, BuiltinExchangeType.DIRECT);
        String deadQueue = "reject-dead-queue";
        channel.queueDeclare(deadQueue, false, false, false, null);
        channel.queueBind(deadQueue, DEAD_EXCHANGE, "reject-dead");

        /*
        正常队列参数
         */
        Map<String, Object> params = new HashMap<>();
        //死信交换机
        params.put("x-dead-letter-exchange", DEAD_EXCHANGE);
        //死信routing key
        params.put("x-dead-letter-routing-key", "reject-dead");

        //正常交换机
        channel.exchangeDeclare(NORMAL_EXCHANGE, BuiltinExchangeType.DIRECT);
        //定义正常队列并绑定
        String normalQueue = "reject-normal-queue";
        channel.queueDeclare(normalQueue, false, false, false, params);
        channel.queueBind(normalQueue, NORMAL_EXCHANGE, "reject-normal");

        log.info("Reject正常消费者等待消费...");

        DeliverCallback deliverCallback = (consumerTag, message) -> {

            String msg = new String(message.getBody(), StandardCharsets.UTF_8);

            //拒绝info5
            if (Objects.equals(msg,"info5")){
                log.info("Reject正常消费者拒绝接收的消息：{}", msg);
                channel.basicReject(message.getEnvelope().getDeliveryTag(),false);
            }else {
                log.info("Reject正常消费者接收的消息：{}", msg);
                channel.basicAck(message.getEnvelope().getDeliveryTag(),false);
            }


        };

        boolean autoAck = false;
        channel.basicConsume(normalQueue, autoAck, deliverCallback, consumerTag -> {
        });
    }


}
