package com.chamc.rabbitmq;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

public class Producer {

	public static void main(String[] args) throws IOException, TimeoutException {
		
		//设置连接基本情况
		ConnectionFactory factory = new ConnectionFactory();
		factory.setUsername("guest");
		factory.setPassword("guest");
		factory.setHost("localhost");
		
		//建立连接
		Connection connection = factory.newConnection();
			
		//建立通道
		Channel channel = connection.createChannel();
				
		//声明exchange
		String exchangeName = "hello-exchange";
		channel.exchangeDeclare(exchangeName, "direct", false);
		
		String routingKey = "hello-route";
		
		//发布消息
		byte[] message = "Hello World".getBytes();
		channel.basicPublish(exchangeName, routingKey, null, message);
		channel.close();
		connection.close();
		
	}
}
