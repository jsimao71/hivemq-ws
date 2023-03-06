package com.hivemq.ws.manager;

import org.springframework.beans.factory.annotation.Autowired;

import com.hivemq.ws.dao.BrokerRepository;
import com.hivemq.ws.domain.Broker;

//@Trasactional
public class BrokerManager {

	@Autowired
	private BrokerRepository repository;
	
	public void save(String id, Broker broker) {
		repository.save(id, broker);
	}

	public Broker find(String id) {
		return repository.find(id);
	}
	
	public Broker remove(String id) {
		return repository.remove(id);
	}
}
