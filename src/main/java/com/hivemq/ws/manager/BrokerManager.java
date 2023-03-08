package com.hivemq.ws.manager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import com.hivemq.ws.dao.BrokerRepository;
import com.hivemq.ws.domain.Broker;
import com.hivemq.ws.util.HiveMQTemplate;
import com.hivemq.ws.web.HiveMQRestController;

//@Trasactional
@Service
public class BrokerManager {

	Logger logger = LoggerFactory.getLogger(HiveMQRestController.class);
	
	@Autowired
	private BrokerRepository repository;
	
	private HiveMQTemplate template = new HiveMQTemplate();

	@Autowired
    private SimpMessagingTemplate smtemplate;
	
	public void save(String id, Broker broker) {
		repository.save(id, broker);
	}

	public Broker find(String id) {
		return repository.find(id);
	}
	
	public Broker remove(String id) {
		return repository.remove(id);
	}
	
	public void send(Broker broker, String topic, Object payload) {
		template.send(broker, topic, payload);
		smtemplate.convertAndSend("/queue/" + topic, payload);	
	}

}
