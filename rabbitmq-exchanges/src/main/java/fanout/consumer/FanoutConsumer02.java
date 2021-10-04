package fanout.consumer;

import api.utils.RabbitMqUtil;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DeliverCallback;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeoutException;

/**
 * @author HuanJu
 * @date 2021/10/2 16:23
 */
@Slf4j
public class FanoutConsumer02 {


    private static final String EXCHANGE_NAME = "logs";

    public static void main(String[] args) throws IOException, TimeoutException {

        Channel channel = RabbitMqUtil.getChannel();

        channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.FANOUT);

        String queueName = channel.queueDeclare().getQueue();

        channel.queueBind(queueName, EXCHANGE_NAME, "");
        log.info("等待接收消息，写入到文件...");
        DeliverCallback deliverCallback = (consumerTag, message) -> {
            String msg = new String(message.getBody(), StandardCharsets.UTF_8);
            File file = new File("D:/testTemp/rabbit_mq_info.txt");
            FileUtils.writeStringToFile(file, msg, StandardCharsets.UTF_8);
            channel.basicAck(message.getEnvelope().getDeliveryTag(), false);
            log.info("文件写入成功");

        };

        channel.basicConsume(queueName,false,deliverCallback,consumerTag -> {});


    }

}
