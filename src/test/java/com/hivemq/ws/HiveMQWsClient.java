/**
 * 
 */
package com.hivemq.ws;

import java.io.IOException;
import java.util.function.Consumer;

import org.springframework.http.HttpMethod;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.ResponseExtractor;
import org.springframework.web.client.RestTemplate;

import com.hivemq.ws.domain.Broker;


/**
 *
 */
public class HiveMQWsClient {

	private String host;
	RestTemplate template;
	
	/**
	 * 
	 */
	public HiveMQWsClient(String host, RestTemplate template) {
		this.host = host;
		this.template = template;
	}

	public HiveMQWsClient(String host) {
		this(host, new RestTemplate());
	}

	public static class Endpoints {

		public static String base(String uri) {
			return uri + "/mqtt";
		}

		public static String broker(String uri, String broker) {
			return base(uri) + "/" + broker;
		}
		
		public static String send(String uri, String broker, String topic) {
			return broker(uri, broker) + "/send/" + topic;
		}
		public static String receive(String uri, String broker, String topic) {
			return broker(uri, broker) + "/receive/" + topic;
		}

	}
	
	public void setupBroker(String id, Broker broker) {
		template.put(Endpoints.broker(host, id), broker);
	}
	
	public Broker getBroker(String id) {
		return template.getForObject(Endpoints.broker(host, id), Broker.class);
	}

	public void deleteBroker(String id) {
		template.delete(Endpoints.broker(host, id));
	}

	public void send(String id, String topic, Object payload) {
		template.postForLocation(Endpoints.send(host, id, topic), payload);		
	}
	
	public <T> T receive(String id, String topic, Class<T> type) {
		return template.getForObject(Endpoints.receive(host, id, topic)+"?n=1", type);
	}

	public void subscribe(String id, String topic, Consumer<ClientHttpResponse> callback) {
		subscribe(id, topic, null, callback);
	}

	public void subscribe(String id, String topic, Integer n, Consumer<ClientHttpResponse> callback) {
		String params = "";
		if (n!=null) {
			params += "n="+n;
		}
		if (!params.isEmpty()) {
			params = "?" + params;
		}
		template.execute(Endpoints.receive(host, id, topic) + params, HttpMethod.GET, null, new ResponseExtractor<Void>() {

					@Override
					public Void extractData(ClientHttpResponse response) throws IOException {
						callback.accept(response);
						return null;
					}
				}
		);		
	}

}
