package direct.consumer;

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
 * @date 2021/10/2 22:57
 */
@Slf4j
public class DirectConsumer01 {

    private static final String EXCHANGE_NAME = "direct_logs";

    private static final String ROUTING_KEY = "error";

    public static void main(String[] args) throws IOException, TimeoutException {

        Channel channel = RabbitMqUtil.getChannel();

        channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.DIRECT);

        String queueName = "disk";


        channel.queueDeclare(queueName, true, false, false, null);


        channel.queueBind(queueName, EXCHANGE_NAME, ROUTING_KEY);

        log.info("等待接收消息，写入文件。。。");

        DeliverCallback deliverCallback = (consumerTag, message) -> {
            String msg = new String(message.getBody(), StandardCharsets.UTF_8);

            msg = "接收绑定键：" + message.getEnvelope().getRoutingKey() + ",消息：" + msg;
            File file = new File("D:/testTemp/rabbitmq_error.txt");
            FileUtils.writeStringToFile(file, msg, StandardCharsets.UTF_8);
            channel.basicAck(message.getEnvelope().getDeliveryTag(), false);
            log.info("错误日志已收集");

        };

        int preFetchCount = 1;

        channel.basicQos(1);


        channel.basicConsume(queueName,false,deliverCallback,consumerTag -> {});


    }


}
