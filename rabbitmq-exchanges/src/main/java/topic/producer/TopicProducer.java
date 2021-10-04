package topic.producer;

import api.utils.RabbitMqUtil;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.MessageProperties;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;

/**
 * @author HuanJu
 * @date 2021/10/3 1:16
 */
@Slf4j
public class TopicProducer {

    private static final String EXCHANGE_NAME = "topic-x";


    public static void main(String[] args) throws IOException, TimeoutException, InterruptedException {

        Channel channel = RabbitMqUtil.getChannel();

        channel.confirmSelect();

        channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.TOPIC);
        /*
        Q1: *.orange.*
        Q2: *.*.rabbit     lazy.#
         */

        Map<String, String> map = new HashMap<>();

        map.put("quick.orange.rabbit", "被Q1Q2接收");
        map.put("lazy.orange.fox", "被Q1Q2接收");
        map.put("follow.white.rabbit", "被Q2接收");
        map.put("lazy", "被Q2接收");
        map.put("lazy.day.aa.bb.cc", "被Q2接收");

        map.put("hello.rabbit.aa.bb", "不被接收");
        map.put("aa.orange.bb", "被Q1接收");


        for (Map.Entry<String, String> entry : map.entrySet()) {

            String key = entry.getKey();

            String value = entry.getValue();

            channel.basicPublish(EXCHANGE_NAME, key, MessageProperties.PERSISTENT_TEXT_PLAIN, value.getBytes(StandardCharsets.UTF_8));

            channel.waitForConfirms();

            log.info("发出消息：{}", value);


        }


    }

}
