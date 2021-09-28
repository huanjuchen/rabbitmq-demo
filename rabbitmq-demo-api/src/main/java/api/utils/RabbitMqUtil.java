package api.utils;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * @author HuanJu
 * @date 2021/9/25 18:13
 */
public class RabbitMqUtil {

    private static Connection connection;

    public static Channel getChannel() throws IOException, TimeoutException {

        if (connection != null && connection.isOpen()) {
            return connection.createChannel();
        }
        ConnectionFactory factory = new ConnectionFactory();

        factory.setHost("192.168.10.99");
        factory.setUsername("admin");
        factory.setPassword("123");
        connection = factory.newConnection();
        return connection.createChannel();
    }

    public static void closeConnection() throws IOException {
        connection.close();
    }


}
