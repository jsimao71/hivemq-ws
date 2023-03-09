/**
 * 
 */
package com.hivemq.ws.dao;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.hivemq.ws.domain.Broker;

/**
 *
 */
public class SimpleBrokerRepository implements BrokerRepository {


	private Map<String, Broker> brokers = new ConcurrentHashMap<>();
	
	/**
	 * 
	 */
	public SimpleBrokerRepository() {
	}

	
	
	/**
	 * @return the brokers
	 */
	public Map<String, Broker> getBrokers() {
		return brokers;
	}

	/**
	 * @param brokers the brokers to set
	 */
	public void setBrokers(Map<String, Broker> brokers) {
		this.brokers = brokers;
	}

	@Override
	public void save(String id, Broker broker) {
		brokers.put(id, broker);
	}

	@Override
	public Broker find(String id) {
		return brokers.get(id);
	}
	
	@Override
	public Broker remove(String id) {
		return brokers.remove(id);
	}
}
