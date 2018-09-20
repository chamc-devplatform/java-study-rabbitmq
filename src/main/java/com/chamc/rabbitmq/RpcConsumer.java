package com.chamc.rabbitmq;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;

public class RpcConsumer {
	public static void main(String[] args) throws IOException, TimeoutException {

		// 设置连接基本配置
		ConnectionFactory factory = new ConnectionFactory();
		factory.setUsername("guest");
		factory.setPassword("guest");
		factory.setHost("localhost");

		// 建立连接
		Connection connection = factory.newConnection();

		// 建立信道
		Channel channel = connection.createChannel();

		// 声明exchange
		String exchangeName = "hello-exchange";
		channel.exchangeDeclare("hello-exchange", "direct", true);

		// 声明队列
		String queueName = channel.queueDeclare().getQueue();
		String routingKey = "hello-route";

		// 绑定
		channel.queueBind(queueName, exchangeName, routingKey);

		System.out.println("正在等待中请稍后");
		channel.basicConsume(queueName, false, "", new DefaultConsumer(channel) {

			@Override
			public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties,
					byte[] body) throws IOException {

				System.out.println("--------------------------------");
				System.out.println("类型:" + properties.getContentType());
				System.out.println("内容:" + new String(body, "UTF-8"));
				System.out.println("请求id:" + properties.getCorrelationId());
				byte[] message = new String("{\"content\": \"我收到了, 返回给你你继续吧\"," + "\"status\":200}").getBytes();
				AMQP.BasicProperties props = new AMQP.BasicProperties().builder().contentType("application/json")
						.correlationId(properties.getCorrelationId()).build();
				channel.basicPublish("", properties.getReplyTo(), props, message);
				channel.basicAck(envelope.getDeliveryTag(), false);
			}

		});

	}
}
