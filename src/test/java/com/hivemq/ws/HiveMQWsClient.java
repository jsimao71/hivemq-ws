/**
 * 
 */
package com.hivemq.ws;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.client.ResponseExtractor;
import org.springframework.web.client.RestTemplate;

import com.hivemq.ws.domain.Broker;
import com.hivemq.ws.web.OctetStreamHttpMessageConverter;


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
		setupConverters(template);
	}
	
	public void setupConverters(RestTemplate template) {	    
	    List<HttpMessageConverter<?>> converters  = new ArrayList<>();
	    converters.add(new OctetStreamHttpMessageConverter());
	    
	    List<HttpMessageConverter<?>> converters_ = template.getMessageConverters();
	    if (converters_!=null) {
		    converters.addAll(converters_);
	    }
	    template.setMessageConverters(converters);		
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
		send(id, topic, convert(payload),  MediaType.APPLICATION_OCTET_STREAM);
	}
	
	public void send(String id, String topic, Object payload, MediaType contentType) {
		String uri = Endpoints.send(host, id, topic);
		RequestEntity<?> request = RequestEntity.post(uri).contentType(contentType).body(payload);
		template.exchange(request, Void.class);		
	}

	protected byte[] convert(Object payload) {
		if (payload instanceof byte[]) {
			return (byte[])payload;
		}
		return payload.toString().getBytes();
	}
	
	public <T> T receive(String id, String topic, Class<T> type) {
		return receive(id, topic, type,  MediaType.APPLICATION_OCTET_STREAM);
	}

	public <T> T receive(String id, String topic, Class<T> type, MediaType accept) {
		String uri = Endpoints.receive(host, id, topic)+"?n=1";
		@SuppressWarnings("unchecked")
		RequestEntity<T> request = (RequestEntity<T>) (accept!=null ? 
				RequestEntity.get(uri).accept(accept).build() :
				RequestEntity.get(uri).build());

		return template.exchange(request, type).getBody();
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
