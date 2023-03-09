/**
 * 
 */
package com.hivemq.ws;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;

import com.hivemq.ws.domain.Broker;
import com.hivemq.ws.util.HiveMQTemplateTests;
import com.hivemq.ws.util.HiveMQTests;

/**
 * curl -X PUT localhost:8080/mqtt/cloud -H "Content-Type: application/json" -d @broker.json
 * curl localhost:8080/mqtt/cloud
 * curl -X POST localhost:8080/mqtt/cloud/send/atopic -H "Content-Type: application/json" -d @msg1.json
 * curl localhost:8080/mqtt/cloud/receive/atopic
 * curl localhost:8080/mqtt/cloud/receive/atopic?n=3
 */
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class HiveMQWsTests {

	
	public String id = "cloud";
	public String topic = "atopic";
	
	@LocalServerPort
	int port = 8081; //8080: default; 8081: TCP/IP monitor
	
	String uri;
	
	
	@BeforeEach
	public void setup() {
		uri = "http://localhost:" + port;
		System.out.println("URI: " + uri);
	}
	

	@Test
	void recvMqtt5Test() {
		recvTest(Broker.MQTT_VERSION_5);
	}
	
	
	@Test
	void recvMqtt3Test() {
		recvTest(Broker.MQTT_VERSION_3);
	}
	
	void recvTest(String version) {
	
		HiveMQWsClient client =  new HiveMQWsClient(uri);
		
		Broker broker = HiveMQTemplateTests.makeBroker(version);
		client.setupBroker(id, broker);
		
		Broker broker2 = client.getBroker(id);
		assertNotNull(broker2);
		assertEquals(broker.getHost(), broker2.getHost());
	
		String payload = "Hello";		
		//System.out.println(Arrays.toString(payload.getBytes()));
		class Sender implements Runnable {
			
			@Override
			public void run() {
				HiveMQTests.sleep(1*1000);
				System.out.println("client.send:" + id + " " + topic + " " + payload);
				client.send(id, topic, payload);
			}
		}
		Thread thread = new Thread(new Sender());
		thread.start();
		byte[] bytes = client.receive(id, topic, byte[].class);
		//System.out.println(Arrays.toString(bytes));
		String payload_ = new String(bytes);
		System.out.println("client.receive:" + payload_);
		assertArrayEquals(payload.getBytes(), payload_.getBytes());

	}

	

	
}
