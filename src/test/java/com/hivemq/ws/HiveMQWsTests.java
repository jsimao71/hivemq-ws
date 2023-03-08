/**
 * 
 */
package com.hivemq.ws;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.function.Consumer;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import com.hivemq.ws.domain.Broker;
import com.hivemq.ws.util.HiveMQTemplateTests;
import com.hivemq.ws.util.HiveMQTests;

/**
 * curl -X PUT localhost:8080/mqtt/cloud -H "Content-Type: application/json" -d @broker.json
 * curl localhost:8080/mqtt/cloud
 * curl -X POST localhost:8080/mqtt/cloud/send/atopic -H "Content-Type: application/json" -d @msg1.json
 * curl localhost:8080/mqtt/cloud/receive/atopic
 */
@SpringBootTest(webEnvironment = WebEnvironment.DEFINED_PORT)
class HiveMQWsTests {

	//@Autowired
	static TaskExecutor taskExecutor;
	
	public String id = "cloud";
	public String topic = "atopic";
	
	//@Value("servlet.port")
	int port = 8080;

	//@Value("servlet.port")
	String port_;
	
	@BeforeAll
	public static void setup() {
		taskExecutor = taskExecutor();
	}
	
	@Test
	@Disabled
	void recvMqtt5Test() {
		recvTest(Broker.MQTT_VERSION_5);
	}
	
	
	@Test
	@Disabled
	void recvMqtt3Test() {
		recvTest(Broker.MQTT_VERSION_3);
	}
	
	void recvTest(String version) {
	
		System.out.println("Port: " + port_);
		HiveMQWsClient client =  new HiveMQWsClient("http://localhost:" + port);
		
		Broker broker = HiveMQTemplateTests.makeBroker(version);
		client.setupBroker(id, broker);
		
		Broker broker2 = client.getBroker(id);
		assertNotNull(broker2);
		assertEquals(broker.getHost(), broker2.getHost());
	
		String payload = "Hello";
		taskExecutor.execute(new Runnable() {
			
			@Override
			public void run() {
				HiveMQTests.sleep(HiveMQTests.WAIT);
				System.out.println("client.send:" + id + " " + topic + " " + payload);
				client.send(id, topic, payload);
			}
		});
		HiveMQTests.sleep(HiveMQTests.WAIT);
		String msg = client.receive(id, topic, String.class);
		assertEquals(msg, payload);
	}

	
	@Test
	@Disabled
	void subscribeMqtt5Test() {
		subscribeTest(Broker.MQTT_VERSION_5);
	}
	
	
	@Test
	@Disabled
	void subscribeMqtt3Test() {
		subscribeTest(Broker.MQTT_VERSION_3);
	}
	
	void subscribeTest(String version) {
	
		HiveMQWsClient client =  new HiveMQWsClient("http://localhost:" + port);
		
		Broker broker = HiveMQTemplateTests.makeBroker(version);
		client.setupBroker(id, broker);
		
		Broker broker2 = client.getBroker(id);
		assertNotNull(broker2);
		assertEquals(broker.getHost(), broker2.getHost());
	
		client.subscribe(id, topic, new Consumer<ClientHttpResponse>() {
			
			@Override
			public void accept(ClientHttpResponse response) {
				try {
					if (response.getStatusCode().value()!=200) {
						System.err.println(response.getStatusCode());
						throw new RuntimeException();
					}
					InputStream in = response.getBody();
					BufferedReader reader = new BufferedReader(new InputStreamReader(in));
					do {
						String line = reader.readLine();
						if (line==null) {
							return;
						}
						System.out.println("Rx: " + line);
					} while (true);
				} catch (IOException e) {
					System.err.println(e);
					throw new RuntimeException(e);
				}
			}
		});

		HiveMQTests.sleep(HiveMQTests.WAIT);
		String payload = "Hello";
		taskExecutor.execute(new Runnable() {
			
			@Override
			public void run() {
				for (int i=0; i<10; i++) {
					String payload_ = payload + i;
					System.out.println("Sending: " + payload_);
					client.send(id, topic, payload_);
				}
			}
		});
		
		HiveMQTests.sleep(HiveMQTests.WAIT);
	}

	public static TaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5);
        executor.setMaxPoolSize(10);
        executor.setQueueCapacity(25);
        executor.afterPropertiesSet();
        return executor;
    }
	
	
	//@Configuration
	static class Config {
		//@Bean
		public TaskExecutor taskExecutor() {
	        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
	        executor.setCorePoolSize(5);
	        executor.setMaxPoolSize(10);
	        executor.setQueueCapacity(25);
	        return executor;
	    }
		
	}
}
