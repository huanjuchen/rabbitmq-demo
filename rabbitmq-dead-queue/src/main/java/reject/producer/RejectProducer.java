package reject.producer;

import api.utils.RabbitMqUtil;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeoutException;

/**
 * @author HuanJu
 * @date 2021/10/4 22:35
 */
@Slf4j
public class RejectProducer {

    private static final String NORMAL_EXCHANGE = "reject-normal-exchange";

    public static void main(String[] args) throws IOException, TimeoutException {
        Channel channel = RabbitMqUtil.getChannel();

        channel.exchangeDeclare(NORMAL_EXCHANGE, BuiltinExchangeType.DIRECT);

        for (int i = 0; i < 10; i++) {
            String msg = "info" + i;
            channel.basicPublish(NORMAL_EXCHANGE, "reject-normal", null, msg.getBytes(StandardCharsets.UTF_8));

            log.info("生产者发送消息：{}", msg);

        }
    }


}
