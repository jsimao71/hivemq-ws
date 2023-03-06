package com.hivemq.ws.util;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.nio.ByteBuffer;

import org.junit.jupiter.api.Test;

import com.hivemq.client.mqtt.MqttClient;
import com.hivemq.client.mqtt.mqtt3.message.publish.Mqtt3Publish;
import com.hivemq.client.mqtt.mqtt5.message.publish.Mqtt5Publish;
import com.hivemq.ws.domain.Broker;

public class HiveMQTemplateTests {
	public static final String host = "e2f060298f8e445e8806e0302c4105af.s2.eu.hivemq.cloud";
	public static final String clientId = null;
	public static final String username = "dev01";
	public static final String password = "Secret33";
	public static final String topic = "testtopic";
	public static final int port=8883;
	public static final int websocketPort = 8884;
    

    public static Broker makeBroker(String version) {
    	return new Broker().withVersion(version)
    			.withHost(host).withPort(port).withWebsocketPort(websocketPort)
    			.withClient(clientId)
    			.withUsername(username).withPassword(password);
    }
    
	@Test
	void mqtt5Test() {
		mqttTest(Broker.MQTT_VERSION_5);
	}
	
	@Test
	void mqtt3Test() {
		mqttTest(Broker.MQTT_VERSION_3);		
	}

		
	void mqttTest(String version) {

		String payload = "Hello";

		HiveMQTests.Result result = new HiveMQTests.Result();
		
		Broker broker = makeBroker(version);

		HiveMQTemplate template = new HiveMQTemplate();
		
		MqttClient client = template.connect(broker, (connAck, throwable) -> {
			if (throwable != null) {
				System.err.println("connectWith.whenComplete: " + throwable);
			} else {
				System.out.println("connectWith.whenComplete: " + connAck);
			}
		});

		assertNotNull(client);

		System.out.println("Connected successfully");

		template.subscribe(broker, client, topic, publish -> {
			ByteBuffer payload_ = null;
			if (broker.isVersion5()) {
				payload_ = ((Mqtt5Publish)publish).getPayload().get();
			} else if (broker.isVersion3()) {
				payload_ = ((Mqtt3Publish)publish).getPayload().get();		
			}
			result.recvCount++;
			result.payload = UTF_8.decode(payload_).toString();   
			System.out.println("Rx: @" + topic + ": " + result.payload);
        }, (subAck, throwable) -> {
            if (throwable != null) {
            	System.err.println("subscribeWith.whenComplete:" + throwable);
            	//throwable.printStackTrace();
            } else {
            	System.out.println("subscribeWith.whenComplete:" + subAck);
            }
        });
		
		HiveMQTests.sleep(HiveMQTests.WAIT);

		System.out.println("Sending...");
        template.send(broker, client, topic, payload);
        result.sentCount++;
        
		HiveMQTests.sleep(HiveMQTests.WAIT);

		assertEquals(result.sentCount, result.recvCount);
		assertEquals(result.payload.toString(), payload);

        template.disconnect(broker, client);
        System.out.println("Done!");
    }

	
	@Test
	void sendMqtt5Test() {
		sendTest(Broker.MQTT_VERSION_5);
	}
	
	@Test
	void sendMqtt3Test() {
		sendTest(Broker.MQTT_VERSION_3);		
	}
	
	void sendTest(String version) {

		String payload = "Hello";

		HiveMQTests.Result result = new HiveMQTests.Result();
		
		Broker broker = makeBroker(version);

		HiveMQTemplate template = new HiveMQTemplate();
		
		System.out.println("Sending...");
        template.send(broker, topic, payload);
        result.sentCount++;
        
		HiveMQTests.sleep(HiveMQTests.WAIT);

        System.out.println("Done!");
    }


}
