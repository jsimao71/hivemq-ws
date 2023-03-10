package com.hivemq.ws.web;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.nio.ByteBuffer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;

import com.hivemq.client.mqtt.mqtt3.message.publish.Mqtt3Publish;
import com.hivemq.client.mqtt.mqtt5.message.publish.Mqtt5Publish;
import com.hivemq.ws.dao.BrokerRepository;
import com.hivemq.ws.domain.Broker;
import com.hivemq.ws.manager.BrokerManager;
import com.hivemq.ws.util.HiveMQTemplate;
import com.hivemq.ws.util.HiveMQTemplate.ClientHolder;

import jakarta.servlet.AsyncContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;


@RestController
@RequestMapping(path = "/mqtt")
public class HiveMQRestController {

	Logger logger = LoggerFactory.getLogger(HiveMQRestController.class);

	@Autowired
	private BrokerRepository repository;
	
	@Autowired
    private BrokerManager manager;

	@PutMapping("/{broker}")
	public ResponseEntity<Void> setupBroker(@PathVariable("broker") String id, @RequestBody  Broker broker) {
		logger.info("setupBroker:" + id + " " + broker);
		repository.save(id, broker);
		return ResponseEntity.noContent().build();
	}

	@GetMapping("/{broker}")
	public Object getBroker(@PathVariable("broker") String id) {
		Broker broker = repository.find(id);
		logger.info("getBroker:" + id + " " + broker);
		return broker;
	}
	
	@DeleteMapping("/{broker}")
	public ResponseEntity<Void> deleteBroker(@PathVariable("broker") String id) {
		logger.info("deleteBroker:" + id );

		Broker broker = repository.remove(id);
		if (broker==null) {
			return ResponseEntity.notFound().build();
		}
		return ResponseEntity.noContent().build();
	}

	@PostMapping(value="/{broker}/send/{topic}", consumes=MediaType.APPLICATION_OCTET_STREAM_VALUE)
	public ResponseEntity<Void> send(@PathVariable("broker") String id, @PathVariable("topic") String topic, @RequestBody byte[] payload) {
		return send(id, topic, (Object)payload);
	}

	@PostMapping("/{broker}/send/{topic}")
	public ResponseEntity<Void> send(@PathVariable("broker") String id, @PathVariable("topic") String topic, @RequestBody  Object payload) {
		logger.info("send:" + id + " " + topic + " " + payload);
		Broker broker = repository.find(id);
		if (broker==null) {
			return ResponseEntity.notFound().build();
		}

		try {
			//logger.info("send:" + id + " " + broker);
			logger.info("send:" + id + " " + payload);

			manager.send(broker, topic, payload);
			
			return ResponseEntity.noContent().build();			
		} catch (RuntimeException e){
			e.printStackTrace();
			throw e;
		}
	}


	@GetMapping("/{broker}/receive/{topic}")
	public DeferredResult<String> receive(@PathVariable("broker") String id, @PathVariable("topic") String topic, 
			@RequestParam(required = false, value="n") Integer n,
			final HttpServletRequest request,
			final HttpServletResponse response) {
		logger.info("receive:" + id + " " + topic);

		long timeout = Long.MAX_VALUE;
		DeferredResult<String> result = new DeferredResult<>(timeout);

		Broker broker = repository.find(id);
		if (broker==null) {
			result.setErrorResult(new RuntimeException());
			return result;
			//return ResponseEntity.notFound().build();
		}
		try {
			String contentType = MediaType.APPLICATION_OCTET_STREAM.toString();
			String accept = request.getHeader(HttpHeaders.ACCEPT);
			if (accept!=null) {
				String[] accepts = accept.split(",");
				if (accepts.length>0) {
					accept = accepts[0];
				}
				//TODO (optional): support integration with HttpMessageConverters
				//contenType = accept;
			}
			response.setHeader(HttpHeaders.CONTENT_TYPE, contentType);

			OutputStream out = response.getOutputStream();

			HiveMQTemplate template = new HiveMQTemplate();
			ClientHolder client = new ClientHolder();
			class IntWrapper {
				int n;
			}
			IntWrapper recv = new IntWrapper();
			client.client = template.connectAndSubscribe(broker, topic, publish -> {
					ByteBuffer payload = null;
					if (broker.isVersion5()) {
						payload = ((Mqtt5Publish)publish).getPayload().get();
					} else if (broker.isVersion3()) {
						payload = ((Mqtt3Publish)publish).getPayload().get();		
					}
					String payload_ = UTF_8.decode(payload).toString();
					System.out.println("Rx: @" + topic + ": " + payload_);
					byte[] bytes = payload_.getBytes();
					try {
						out.write(bytes);
						out.flush();
					} catch (IOException e) {
						System.err.println(e);
						result.setErrorResult(e);
		                template.disconnect(broker, client.client);
					} catch (RuntimeException e) {
						System.err.println(e);
						//result.setErrorResult(new RuntimeException());
		                //template.disconnect(broker, client.client);
					}
					recv.n++;
					if (n!=null && n.equals(recv.n)) {
						result.setResult("");
		                template.disconnect(broker, client.client);
					}
			}, (connAck, throwable) -> {
				if (throwable!=null) {
					System.err.println(throwable);
					result.setErrorResult(throwable);
	                template.disconnect(broker, client.client);
				}
			});
			
			if (client.client==null) {
				result.setErrorResult(new RuntimeException());
				return result;
				//return ResponseEntity.badRequest().build();
			}
		} catch (Exception e){
			System.err.println(e);
			result.setErrorResult(e);
		}	
		return result;
	}

	
	//alt implementation TODO: implement as Servlet
	@GetMapping("/{broker}/receive_/{topic}")
	public void receive2(@PathVariable("broker") String id, @PathVariable("topic") String topic, final HttpServletRequest request) throws IOException {
		logger.info("receive_:" + id + " " + topic);
		AsyncContext asyncContext = request.startAsync();

		logger.info("receive_:" + id + " " + topic);


		Broker broker = repository.find(id);
		if (broker==null) {
			 HttpServletResponse response = (HttpServletResponse) asyncContext.getResponse();
			 response.sendError(404, "Not Found");
			 asyncContext.complete();
			 return;
		}
		HiveMQTemplate template = new HiveMQTemplate();
		ClientHolder client = new ClientHolder();
		client.client = template.connectAndSubscribe(broker, topic, publish -> {
			ByteBuffer payload = null;
			if (broker.isVersion5()) {
				payload = ((Mqtt5Publish)publish).getPayload().get();
			} else if (broker.isVersion3()) {
				payload = ((Mqtt3Publish)publish).getPayload().get();		
			}
			String payload_ = UTF_8.decode(payload).toString();
			System.out.println("Rx_: @" + topic + ": " + payload_);

			try {
				OutputStream out = asyncContext.getResponse().getOutputStream();
				PrintWriter writer = new PrintWriter(out);
				writer.println(payload_);
				writer.flush();
			} catch (Exception e) {
				System.err.println(e);
				//result.setErrorResult(new RuntimeException());
                //template.disconnect(broker, client.client);
			}
		}, (connAck, throwable) -> {
			if (throwable!=null) {
				System.err.println(throwable);
				 HttpServletResponse response = (HttpServletResponse) asyncContext.getResponse();
				 try {
					 response.sendError(400, "Bad Request");
				} catch (IOException e) {
					System.err.println(e);
				}
				asyncContext.complete();
	            template.disconnect(broker, client.client);
			}
		});
		

		if (client.client==null) {
			 HttpServletResponse response = (HttpServletResponse) asyncContext.getResponse();
			 response.sendError(400, "Bad Request");
			 asyncContext.complete();
			 return;
		}


	}



}
