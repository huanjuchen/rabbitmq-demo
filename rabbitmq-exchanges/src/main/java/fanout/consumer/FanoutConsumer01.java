package fanout.consumer;

import api.utils.RabbitMqUtil;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DeliverCallback;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeoutException;

/**
 * @author HuanJu
 * @date 2021/9/29 23:09
 */
@Slf4j
public class FanoutConsumer01 {

    private static final String EXCHANGE_NAME = "logs";

    public static void main(String[] args) throws IOException, TimeoutException {
        Channel channel = RabbitMqUtil.getChannel();
        //定义交换机
        channel.exchangeDeclare(EXCHANGE_NAME, "fanout");
        /*
        定义临时队列
        名称随机
        消费者断开自动删除
         */
        String queue = channel.queueDeclare().getQueue();
        /*
        绑定队列到交换机，routingKey（binding Key）为空
         */
        channel.queueBind(queue, EXCHANGE_NAME, "");


        int preFetchCount = 10;
        channel.basicQos(10);
        log.info("等待接收消息，打印到控制台...");

        DeliverCallback deliverCallback = (consumerTag, message) -> {
            String msg = new String(message.getBody(), StandardCharsets.UTF_8);

            log.info("接收消息：{}", msg);
            channel.basicAck(message.getEnvelope().getDeliveryTag(), false);
        };

        channel.basicConsume(queue, false, deliverCallback, consumerTag -> {
        });


    }


}
