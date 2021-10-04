package topic.consumer;

import api.utils.RabbitMqUtil;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DeliverCallback;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeoutException;

/**
 * @author HuanJu
 * @date 2021/10/3 1:10
 */
@Slf4j
public class TopicConsumer02 {

    private static final String EXCHANGE_NAME = "topic-x";

    /**
     * 最后为rabbit的 3个单词的字符串
     */
    private static final String TOPIC_KEY1 = "*.*.rabbit";

    /**
     * 匹配第一个单词为lazy的多个单词字符串
     */

    private static final String TOPIC_KEY2 = "lazy.#";

    public static void main(String[] args) throws IOException, TimeoutException {

        Channel channel = RabbitMqUtil.getChannel();

        channel.exchangeDeclare(EXCHANGE_NAME,"topic");

        String queueName = "topic-q2";

        channel.queueDeclare(queueName,true,false,false,null);

        channel.queueBind(queueName,EXCHANGE_NAME,TOPIC_KEY1);
        channel.queueBind(queueName,EXCHANGE_NAME,TOPIC_KEY2);

        log.info("Q2等待接收消息...");
        DeliverCallback deliverCallback = (consumerTag, message) -> {
            String msg = new String(message.getBody(), StandardCharsets.UTF_8);

            log.info("接收队列：{}，绑定键：{}，消息：{}", queueName,
                    message.getEnvelope().getRoutingKey(), msg);

            channel.basicAck(message.getEnvelope().getDeliveryTag(), false);
        };

        channel.basicQos(1);

        channel.basicConsume(queueName,false,deliverCallback,consumerTag -> {});
    }


}
