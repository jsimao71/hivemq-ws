package com.hivemq.ws.util;

import static com.hivemq.client.mqtt.MqttGlobalPublishFilter.ALL;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import com.hivemq.client.mqtt.MqttClient;
import com.hivemq.client.mqtt.mqtt3.Mqtt3AsyncClient;
import com.hivemq.client.mqtt.mqtt5.Mqtt5BlockingClient;

public class HiveMQTests {
	public static final String host = "e2f060298f8e445e8806e0302c4105af.s2.eu.hivemq.cloud";
	public static final String clientId = null;
	public static final String username = "dev01";
	public static final String password = "Secret33";
	public static final String topic = "atopic";
	public static final int port=8883;
	public static final int websocketPort = 8884;
	public  static int WAIT = 2*1000;
	
	public static class Result {
		int sentCount;
		int recvCount;
		String payload;
	}
	
	@Test
	void mqtt3Test() {
		String payload = "Hello";

		Result result = new Result();
		
		@SuppressWarnings("deprecation")
		Mqtt3AsyncClient client = MqttClient.builder()
				.useMqttVersion3()
				//.useMqttVersion5()
				//.identifier(clientId)
				.serverHost(host)
				.serverPort(port)
				.useSslWithDefaultConfig()
				.buildAsync();

		client.connectWith()
		.simpleAuth()
			.username(username)
			.password(UTF_8.encode(password))
			//.password(password.getBytes())
			.applySimpleAuth()
		.send()
		.whenComplete((connAck, throwable) -> {
			if (throwable != null) {
				System.err.println("connectWith.whenComplete:" + throwable);
			} else {
				System.out.println("connectWith.whenComplete:" + connAck);
			}
		});


		System.out.println("Connected successfully");

		/*client.subscribeWith()
		.topicFilter(topic)
		.callback(publish -> {
			System.out.println("subscribeWith.callback:" + publish);
		})
		.send()
		.whenComplete((subAck, throwable) -> {
			if (throwable != null) {
				System.err.println("subscribeWith.whenComplete:" + throwable);
				//throwable.printStackTrace();
			} else {
				System.out.println("subscribeWith.whenComplete:" + subAck);
				// Handle successful subscription, e.g. logging or incrementing a metric
			}
		   
		});*/

		client.subscribeWith()
		.topicFilter(topic)
		.send();
		
		// set a callback that is called when a message is received (using the async API style)
		client.toAsync().publishes(ALL, publish -> {
			result.payload = UTF_8.decode(publish.getPayload().get()).toString();
			System.out.println("Rx: @" + publish.getTopic() + ": " + result.payload);
			result.recvCount++;
			//client.disconnect();
		});
		sleep(WAIT);
		
		System.out.println("Sending...");
		client.publishWith()
				.topic(topic)
				//.payload(UTF_8.encode(payload))
				.payload(payload.getBytes())
				.send();
		result.sentCount++;
		
		sleep(WAIT);

		assertEquals(result.sentCount, result.recvCount);
		assertEquals(result.payload, payload);
		
		System.out.println("Disconnecting...");
		client.disconnect();
		System.out.println("Done!");
	}

	
	@Test
	void mqtt5Test() {
		String payload = "Hello";
		
		Result result = new Result();
		// create an MQTT client
		final Mqtt5BlockingClient client = MqttClient.builder()
				.useMqttVersion5()
				.serverHost(host)
				.serverPort(port)
				.sslWithDefaultConfig()
				.buildBlocking();

		// connect to HiveMQ Cloud with TLS and username/pw
		client.connectWith()
				.simpleAuth()
				.username(username)
				.password(UTF_8.encode(password))
				.applySimpleAuth()
				.send();

		System.out.println("Connected successfully");

		// subscribe to the topic
		client.subscribeWith()
				.topicFilter(topic)
				.send();

		// set a callback that is called when a message is received (using the async API style)
		client.toAsync().publishes(ALL, publish -> {
			result.payload = UTF_8.decode(publish.getPayload().get()).toString();
			System.out.println("Rx: @" + publish.getTopic() + ": " + result.payload);
			result.recvCount++;
		});

		sleep(WAIT);
		
		System.out.println("Sending...");

		// publish a message to the topic
		client.publishWith()
				.topic(topic)
				.payload(UTF_8.encode(payload))
				.send();
		result.sentCount++;

		sleep(WAIT);
		assertEquals(result.sentCount, result.recvCount);
		assertEquals(result.payload, payload);

		System.out.println("Disconnecting...");
		client.disconnect();
		System.out.println("Done!");

	}
	
	public static void sleep(int milis) {
		System.out.println("Waiting " + milis + "...");
		try {
			Thread.sleep(10*1000);
		} catch (InterruptedException e) {
		}
	}

}
