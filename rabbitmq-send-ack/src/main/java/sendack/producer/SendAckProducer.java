package sendack.producer;

import api.utils.RabbitMqUtil;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.ConfirmCallback;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ConcurrentNavigableMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.TimeoutException;

/**
 * @author HuanJu
 * @date 2021/9/28 21:23
 */
@Slf4j
public class SendAckProducer {

    private static final String SEND_ACK_QUEUE = "send_ack_queue";

    private static final int MSG_COUNT = 1000;

    public static void main(String[] args) throws IOException, TimeoutException, InterruptedException {
        singleSyncAck();
        batchSyncAck();
        asyncAck();

    }

    /**
     * 单个消息同步确认
     */
    public static void singleSyncAck() throws IOException, TimeoutException, InterruptedException {
        Channel channel = RabbitMqUtil.getChannel();

        channel.queueDeclare(SEND_ACK_QUEUE, false, false, false, null);
        //开启发布确认
        channel.confirmSelect();
        long begin = System.currentTimeMillis();
        for (int i = 0; i < MSG_COUNT; i++) {

            String msg = i + "";

            channel.basicPublish("", SEND_ACK_QUEUE, null, msg.getBytes(StandardCharsets.UTF_8));

            boolean flag = channel.waitForConfirms();

            if (flag) {
                log.info("singleSyncAck 消息发布成功");
            }
        }
        long end = System.currentTimeMillis();
        log.info("发布{}个单独确认消息，耗时{}ms", MSG_COUNT, end - begin);

    }


    public static void batchSyncAck() throws IOException, TimeoutException, InterruptedException {
        Channel channel = RabbitMqUtil.getChannel();

        channel.queueDeclare(SEND_ACK_QUEUE, false, false, false, null);

        //开启发布确认
        channel.confirmSelect();
        //批量确认大小
        int batchSize = 100;

        int noAckCount = 0;

        long begin = System.currentTimeMillis();
        for (int i = 0; i < MSG_COUNT; i++) {
            String msg = i + "";
            channel.basicPublish("", SEND_ACK_QUEUE, null, msg.getBytes(StandardCharsets.UTF_8));
            noAckCount++;
            if (batchSize == noAckCount) {
                channel.waitForConfirms();

                noAckCount = 0;
            }
        }

        //为了保证没有剩余确认消息，再次确认
        if (noAckCount > 0) {
            channel.waitForConfirms();
        }

        long end = System.currentTimeMillis();


        log.info("发布{}个批量确认消息，耗时{}ms", MSG_COUNT, end - begin);
    }


    /**
     * 异步确认
     */
    public static void asyncAck() throws IOException, TimeoutException {

        Channel channel = RabbitMqUtil.getChannel();
        channel.queueDeclare(SEND_ACK_QUEUE, false, false, false, null);
        //开启发布确认

        channel.confirmSelect();

        /*
            线程安全有序的一个哈希表，适用于高并发
            1.轻松的将序号与消息关联
            2.轻松批量删除条目 只要给到序列号
            3.支持并发访问
         */
        ConcurrentSkipListMap<Long, String> outStandingConfirms = new ConcurrentSkipListMap<>();

        /*
        确认回调
        1.消息序列号
        2.true 可以确认 小于或等于当前序列号的消息
          false 确认当前序列号消息
         */
        ConfirmCallback confirmCallback = (deliveryTag, multiple) -> {
            if (multiple) {
                //确认 小于或等于当前序列号消息
                //返回的未确认消息是一个map
                ConcurrentNavigableMap<Long, String> confirmed = outStandingConfirms.headMap(deliveryTag, true);
                //清除该部分未确认消息
                confirmed.clear();
            } else {
                //只清除当前消息
                outStandingConfirms.remove(deliveryTag);
            }
        };


        /*
        未确认回调
         */
        ConfirmCallback nackCallback = (deliveryTag, multiple) -> {
            String message = outStandingConfirms.get(deliveryTag);
            log.info("发布的消息{}未被确认，序列号{}", message, deliveryTag);
        };

        /*
        添加异步确认监听器
         */
        channel.addConfirmListener(confirmCallback, nackCallback);

        long begin = System.currentTimeMillis();

        for (int i = 0; i < MSG_COUNT; i++) {
            String message = "消息" + i;

            /*
            channel.getNextPublishSeqNo() 获取下一个消息序列号

             */

            outStandingConfirms.put(channel.getNextPublishSeqNo(), message);
            channel.basicPublish("", SEND_ACK_QUEUE, null, message.getBytes(StandardCharsets.UTF_8));
        }

        long end = System.currentTimeMillis();


        log.info("发布{}个异步确认消息，耗时{}ms", MSG_COUNT, end - begin);


    }


}
