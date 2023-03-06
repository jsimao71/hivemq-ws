package com.hivemq.ws.util;


import static java.nio.charset.StandardCharsets.UTF_8;

import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import com.hivemq.client.mqtt.MqttClient;
import com.hivemq.client.mqtt.mqtt3.Mqtt3AsyncClient;
import com.hivemq.client.mqtt.mqtt3.Mqtt3ClientBuilder;
import com.hivemq.client.mqtt.mqtt3.message.connect.connack.Mqtt3ConnAck;
import com.hivemq.client.mqtt.mqtt3.message.publish.Mqtt3Publish;
import com.hivemq.client.mqtt.mqtt3.message.subscribe.suback.Mqtt3SubAck;
import com.hivemq.client.mqtt.mqtt5.Mqtt5AsyncClient;
import com.hivemq.client.mqtt.mqtt5.Mqtt5ClientBuilder;
import com.hivemq.client.mqtt.mqtt5.message.connect.connack.Mqtt5ConnAck;
import com.hivemq.client.mqtt.mqtt5.message.publish.Mqtt5Publish;
import com.hivemq.client.mqtt.mqtt5.message.subscribe.suback.Mqtt5SubAck;
import com.hivemq.ws.domain.Broker;


public class HiveMQTemplate {


	private Broker broker;
	
	/**
	 * 
	 */
	public HiveMQTemplate() {
	}

	public static class ClientHolder {
		public MqttClient client;
	}

	
	/**
	 * 
	 */
	public HiveMQTemplate(Broker broker) {
		this.broker = broker;
	}
	
	

	/**
	 * @return the broker
	 */
	public Broker getBroker() {
		return broker;
	}

	/**
	 * @param broker the broker to set
	 */
	public void setBroker(Broker broker) {
		this.broker = broker;
	}

	@SuppressWarnings("deprecation")
	public MqttClient makeMqttClient(Broker broker) {
		String version = broker.getVersion();
		if (version==null) {
			version = Broker.MQTT_DEFAULT_VERSION;
		}
		switch (broker.getVersion()) {
		case Broker.MQTT_VERSION_5: {
			Mqtt5ClientBuilder builder = MqttClient.builder()
			.useMqttVersion5()
			.serverHost(broker.getHost())
			.serverPort(broker.getPort())
			.useSslWithDefaultConfig();
			if (broker.getClient()!=null) {
				builder.identifier(broker.getClient());
			}
			return builder.buildAsync();
		}
		case Broker.MQTT_VERSION_3: {
			Mqtt3ClientBuilder builder = MqttClient.builder()
			.useMqttVersion3()
			.serverHost(broker.getHost())
			.serverPort(broker.getPort())
			.useSslWithDefaultConfig();
			if (broker.getClient()!=null) {
				builder.identifier(broker.getClient());
			}
			return builder.buildAsync();
		}
		default:
			throw new MqttVersionException();
		}
		
	}

	public MqttClient connect(Broker broker) {
		return connect(broker, (BiConsumer<?, ? super Throwable>)null);
	}

	public MqttClient connect(Broker broker, BiConsumer<?, ? super Throwable> action) {
		MqttClient client = makeMqttClient(broker);
		return connect(broker, client, action);
	}

	public MqttClient connectAndSubscribe(Broker broker, String topic, Consumer<?> callback) {
		return connectAndSubscribe(broker, topic, callback, null);
	}

	public MqttClient connectAndSubscribe(Broker broker, String topic, Consumer<?> callback, BiConsumer<?, ? super Throwable> action) {
		return connectAndSubscribe(broker, topic, callback, action, action);
	}

	@SuppressWarnings("unchecked")
	public MqttClient connectAndSubscribe(Broker broker, String topic, Consumer<?> callback, BiConsumer<?, ? super Throwable> action, BiConsumer<?, ? super Throwable> connectAction) {
		ClientHolder client = new ClientHolder();
		client.client = connect(broker, (connAck, throwable) -> {
			if (throwable != null) {
				((BiConsumer<Object, ? super Throwable>)connectAction).accept(connAck, throwable);
			} else {
				subscribe(broker, client.client, topic, callback, action);
			}
		});
		return client.client;
	}

	public MqttClient connect(Broker broker, MqttClient client) {
		return connect(broker, client, null);
	}

	@SuppressWarnings("unchecked")
	public MqttClient connect(Broker broker, MqttClient client, BiConsumer<?, ? super Throwable> action) {
		if (client instanceof Mqtt5AsyncClient) {
			return connect(broker, (Mqtt5AsyncClient)client, (BiConsumer<? super Mqtt5ConnAck, ? super Throwable>)action);
		}
		if (client instanceof Mqtt3AsyncClient) {
			return connect(broker, (Mqtt3AsyncClient)client, (BiConsumer<? super Mqtt3ConnAck, ? super Throwable>)action);
		}
		throw new MqttVersionException();		
	}

	public Mqtt5AsyncClient connect(Broker broker, Mqtt5AsyncClient client, BiConsumer<? super Mqtt5ConnAck, ? super Throwable> action) {
		CompletableFuture<Mqtt5ConnAck> future = client.connectWith()
		.simpleAuth()
			.username(broker.getUsername())
			.password(UTF_8.encode(broker.getPassword()))
			.applySimpleAuth()
		.send();
		
		if (action!=null) {
			future.whenComplete(action);
		}
		
		return client;
	}

	public Mqtt3AsyncClient connect(Broker broker, Mqtt3AsyncClient client, BiConsumer<? super Mqtt3ConnAck, ? super Throwable> action) {
		CompletableFuture<Mqtt3ConnAck> future = client.connectWith()
		.simpleAuth()
			.username(broker.getUsername())
			.password(UTF_8.encode(broker.getPassword()))
			.applySimpleAuth()
		.send();

		if (action!=null) {
			future.whenComplete(action);
		}
		
		return client;
	}

	public MqttClient subscribe(Broker broker, String topic, Consumer<?> callback) {
		return subscribe(broker, topic, callback, null);
	}

	public MqttClient subscribe(Broker broker, String topic, Consumer<?> callback, BiConsumer<?, ? super Throwable> action) {
		MqttClient client = connect(broker);
		subscribe(broker, client, topic, callback, action);
		return client;
	}

	public void subscribe(Broker broker, MqttClient client, String topic, Consumer<?> callback) {
		subscribe(broker,  client,  topic, callback, null);
	}


	@SuppressWarnings("unchecked")
	public void subscribe(Broker broker, MqttClient client, String topic, Consumer<?> callback, BiConsumer<?, ? super Throwable> action) {
		if (client instanceof Mqtt5AsyncClient) {
			subscribe(broker, (Mqtt5AsyncClient)client, topic, (Consumer<Mqtt5Publish>)callback, (BiConsumer<? super Mqtt5SubAck, ? super Throwable>)action);
		} else if (client instanceof Mqtt3AsyncClient) {
			subscribe(broker, (Mqtt3AsyncClient)client, topic, (Consumer<Mqtt3Publish>)callback, (BiConsumer<? super Mqtt3SubAck, ? super Throwable>)action);
		} else {
			throw new MqttVersionException();			
		}
	}

	public void subscribe(Broker broker, Mqtt5AsyncClient client, String topic, Consumer<Mqtt5Publish> callback) {
		subscribe(broker, client, topic, callback, null);
	}

	public void subscribe(Broker broker, Mqtt5AsyncClient client, String topic, 
			Consumer<Mqtt5Publish> callback, BiConsumer<? super Mqtt5SubAck, ? super Throwable> action) {
		CompletableFuture<Mqtt5SubAck> future =
		client.subscribeWith()
			.topicFilter(topic)
			.callback(callback)
			.send();
		
		if (action!=null) {
			future.whenComplete(action);
		}

		//client.toAsync().publishes(ALL, callback);
	}

	
	public void subscribe(Broker broker, Mqtt3AsyncClient client, String topic, Consumer<Mqtt3Publish> callback) {
		subscribe(broker, client, topic, callback, null);
	}

	public void subscribe(Broker broker, Mqtt3AsyncClient client, String topic, 
			Consumer<Mqtt3Publish> callback, BiConsumer<? super Mqtt3SubAck, ? super Throwable> action) {
		CompletableFuture<Mqtt3SubAck> future =
		client.subscribeWith()
			.topicFilter(topic)
			.callback(callback)
			.send();

		if (action!=null) {
			future.whenComplete(action);
		}

		//client.toAsync().publishes(ALL, callback);
	}

	public void send(Broker broker, MqttClient client, String topic, Object payload) {
		if (client instanceof Mqtt5AsyncClient) {
			((Mqtt5AsyncClient)client).publishWith()
			.topic(topic)
			.payload(convert(payload))
			.send();
		} else if (client instanceof Mqtt3AsyncClient) {
			((Mqtt3AsyncClient)client).publishWith()
			.topic(topic)
			.payload(convert(payload))
			.send();
		} else {
			throw new MqttVersionException();
		}

	}

	public MqttClient send(Broker broker, String topic, Object payload) {
		return send(broker, topic, payload, null);
	}

	@SuppressWarnings("unchecked")
	public MqttClient send(Broker broker, String topic, Object payload,  BiConsumer<?, ? super Throwable> action) {
		ClientHolder client = new ClientHolder();
		
		client.client = connect(broker, (connAck, throwable) -> {
			if (throwable != null) {
				if (action!=null) {
					((BiConsumer<Object, ? super Throwable>)action).accept(connAck, throwable);
				}
			} else {
				send(broker, client.client, topic, payload);
				if (action!=null) {
					((BiConsumer<Object, ? super Throwable>)action).accept(connAck, throwable);
				}
				disconnect(broker, client.client);
			}
		});
		return client.client;
	}
	

	public void disconnectAfter(Broker broker, MqttClient client, long millis) {
		if (millis>0) {
			try {
				Thread.sleep(millis);
			} catch (InterruptedException e) {
			}			
		}
		disconnect(broker, client);
	}
	
	public void disconnect(Broker broker, MqttClient client) {
		if (client instanceof Mqtt5AsyncClient) {
			((Mqtt5AsyncClient)client).disconnect();
		} else if (client instanceof Mqtt3AsyncClient) {
			((Mqtt3AsyncClient)client).disconnect();
		} else {
			throw new MqttVersionException();
		}
		
	}
	protected byte[] convert(Object payload) {
		if (payload instanceof byte[]) {
			return (byte[])payload;
		}
		return payload.toString().getBytes();
	}

}
