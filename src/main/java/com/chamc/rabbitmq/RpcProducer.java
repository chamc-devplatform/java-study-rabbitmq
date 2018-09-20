package com.chamc.rabbitmq;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.TimeoutException;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;

public class RpcProducer {

	public static void main(String[] args) throws IOException, TimeoutException {

		ConnectionFactory factory = new ConnectionFactory();
		factory.setUsername("guest");
		factory.setPassword("guest");
		factory.setHost("localhost");

		// 建立连接
		Connection connection = factory.newConnection();

		// 建立通道
		Channel channel = connection.createChannel();

		// 声明exchange
		String exchangeName = "hello-exchange";
		channel.exchangeDeclare(exchangeName, "direct", true);

		String routingKey = "hello-route";

		// 构造properties
		String id = UUID.randomUUID().toString();
		AMQP.BasicProperties properties = new AMQP.BasicProperties().builder().contentType("application/json")
				.correlationId(id).replyTo("amq.rabbitmq.reply-to").build();
		
		//监听返回队列
		channel.basicConsume("amq.rabbitmq.reply-to", true, "", new DefaultConsumer(channel) {
			@Override
			public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties,
					byte[] body) throws IOException {
				if (properties.getCorrelationId().equals(id)) {
					System.out.println("返回值：" + new String(body, "UTF-8"));
				}
			}
		});

		// 发布消息
		byte[] message = "{\"name\":\"Hello World\"}".getBytes();
		channel.basicPublish(exchangeName, routingKey, properties, message);

		System.out.println("正在发送，请稍后");

		//channel.close();
		//connection.close();

	}
}
