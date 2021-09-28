package producer;

import api.utils.RabbitMqUtil;
import com.rabbitmq.client.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeoutException;

/**
 * @author HuanJu
 * @date 2021/9/25 18:58
 */
public class HelloWorldProducer {

    private final static Logger log = LoggerFactory.getLogger(HelloWorldProducer.class);

    private final static String QUEUE_NAME = "hello";


    public static void main(String[] args) throws IOException, TimeoutException {


        Channel channel = RabbitMqUtil.getChannel();

        /*
        生成一个队列
        1.队列名称
        2.队列里面的消息是否持久化 默认消息存储在内存中
        3.该队列是否只供一个消费者进行消费
        4.是否自动删除 最后一个消费者断开连接后 队列自动删除
        5.其他参数
         */
        channel.queueDeclare(QUEUE_NAME,false,false,false,null);
        String msg = "hello world";
        /*
        发送一个消息
        1.交换机名
        2.路由key
        3.其他参数信息
        4.消息体
         */
        channel.basicPublish("",QUEUE_NAME,null,msg.getBytes(StandardCharsets.UTF_8));

        log.info("消息发送完毕...");
        channel.close();
        RabbitMqUtil.closeConnection();

    }

}
