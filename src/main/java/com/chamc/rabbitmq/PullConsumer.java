package com.chamc.rabbitmq;

import java.io.IOException;
import java.util.Scanner;
import java.util.concurrent.TimeoutException;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.GetResponse;

public class PullConsumer {

	public static void main(String[] args) throws IOException, TimeoutException {
		ConnectionFactory factory = new ConnectionFactory();
		factory.setAutomaticRecoveryEnabled(true);
		factory.setHost("10.1.1.135");
		factory.setPort(8002);
		factory.setUsername("root");
		factory.setPassword("123456");
		factory.setVirtualHost("vhost_one");
		
		Connection connection = factory.newConnection();
		Channel channel = connection.createChannel();
		
		channel.exchangeDeclare("hello-exchange", "direct", true);
		channel.queueDeclare("hello-queue", true, false, false, null);
		channel.queueBind("hello-queue", "hello-exchange", "hello-route");
		
		Scanner in = new Scanner(System.in);
		System.out.println(in.nextLine());
		in.close();
		
		while(true) {
			GetResponse response = channel.basicGet("hello-queue", false);
			if(response == null) {
				break;
			}
			System.out.println("--------------------");
			System.out.println("count:" + response.getMessageCount());
			System.out.println("properties:" + response.getProps());
			System.out.println("content:" + new String(response.getBody(), "UTF-8"));
			channel.basicAck(response.getEnvelope().getDeliveryTag(), false);
		}
		
		channel.close();
		connection.close();
		
		
	}

}
