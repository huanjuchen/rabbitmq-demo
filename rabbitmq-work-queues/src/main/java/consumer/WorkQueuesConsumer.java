package consumer;

import api.utils.RabbitMqUtil;
import com.rabbitmq.client.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;
import java.util.concurrent.TimeoutException;


/**
 * @author HuanJu
 * @date 2021/9/25 20:33
 */
public class WorkQueuesConsumer {

    private static final String QUEUE_NAME = "hello";
    private static final Logger log = LoggerFactory.getLogger(WorkQueuesConsumer.class);

    public static void main(String[] args) throws IOException, TimeoutException {
        Channel channel = RabbitMqUtil.getChannel();
        Scanner scanner = new Scanner(System.in);

        while (scanner.hasNext()){
            String msg = scanner.next();
            channel.basicPublish("",QUEUE_NAME,null,msg.getBytes(StandardCharsets.UTF_8));
            log.info("消息发送成功...");
        }

    }

}
