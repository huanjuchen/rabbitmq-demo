package direct.producer;

import api.utils.RabbitMqUtil;
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
 * @date 2021/10/3 0:06
 */
@Slf4j
public class DirectProducer {

    private static final String EXCHANGE_NAME = "direct_logs";


    public static void main(String[] args) throws IOException, TimeoutException, InterruptedException {

        Channel channel = RabbitMqUtil.getChannel();

        channel.exchangeDeclare(EXCHANGE_NAME, "direct");

        Map<String, String> map = new HashMap<>();
        map.put("info", "普通Info信息");
        map.put("warning", "警告warning信息");
        map.put("error", "错误error信息");
        map.put("debug", "调试debug信息");

        channel.confirmSelect();

        for (Map.Entry<String, String> entry : map.entrySet()) {
            String key = entry.getKey();
            String msg = entry.getValue();


            channel.basicPublish(EXCHANGE_NAME, key, MessageProperties.PERSISTENT_TEXT_PLAIN, msg.getBytes(StandardCharsets.UTF_8));

            channel.waitForConfirms();

            log.info("生产者发出消息：{}", msg);


        }


    }

}
