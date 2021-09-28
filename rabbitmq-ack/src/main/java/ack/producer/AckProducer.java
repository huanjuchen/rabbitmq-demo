package ack.producer;

import api.utils.RabbitMqUtil;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;


import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;
import java.util.concurrent.TimeoutException;

/**
 * @author HuanJu
 * @date 2021/9/26 23:46
 */
@Slf4j
public class AckProducer {


    private static final String TASK_QUEUE_NAME = "ack_queue";

    public static void main(String[] args) throws IOException, TimeoutException {
        Channel channel = RabbitMqUtil.getChannel();
        //定义队列
        channel.queueDeclare(TASK_QUEUE_NAME,false,false,false,null);

        Scanner sc = new Scanner(System.in);
        System.out.println("请输入消息");
        while (sc.hasNext()) {
            String msg = sc.nextLine();
            channel.basicPublish("", TASK_QUEUE_NAME, null, msg.getBytes(StandardCharsets.UTF_8));
            log.info("生产者发出消息：{}", msg);
        }
    }


}
