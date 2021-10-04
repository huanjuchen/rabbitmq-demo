package fanout.producer;

import api.utils.RabbitMqUtil;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;
import java.util.concurrent.TimeoutException;

/**
 * @author HuanJu
 * @date 2021/10/2 16:32
 */
@Slf4j
public class FanoutProducer {

    private static final String EXCHANGE_NAME = "logs";


    public static void main(String[] args) throws IOException, TimeoutException, InterruptedException {

        Channel channel = RabbitMqUtil.getChannel();


        channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.FANOUT);

        channel.confirmSelect();

        Scanner scanner = new Scanner(System.in);

        System.out.println("请输入信息");

        while (scanner.hasNext()) {
            String msg = scanner.nextLine();
            channel.basicPublish(EXCHANGE_NAME, "", null, msg.getBytes(StandardCharsets.UTF_8));

            channel.waitForConfirms();
            log.info("生产者发送消息：{}", msg);
        }


    }

}
