package nofair.producer;

import api.utils.RabbitMqUtil;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.MessageProperties;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;
import java.util.concurrent.TimeoutException;

/**
 * @author HuanJu
 * @date 2021/9/27 23:29
 */
@Slf4j
public class NoFairProducer {

    private static final String NO_FAIR_QUEUES = "no_fair_queues";

    public static void main(String[] args) throws IOException, TimeoutException {

        Channel channel = RabbitMqUtil.getChannel();
        //定义持久化队列
        channel.queueDeclare(NO_FAIR_QUEUES, true, false, false, null);
        System.out.println("请输入消息");
        Scanner sc = new Scanner(System.in);
        while (sc.hasNext()) {
            String msg = sc.nextLine();
            //持久化发送
            channel.basicPublish("", NO_FAIR_QUEUES, MessageProperties.PERSISTENT_TEXT_PLAIN, msg.getBytes(StandardCharsets.UTF_8));
            log.info("已发送消息：{}", msg);
        }
    }


}
