/**
 * 
 */
package com.hivemq.ws.dao;

import com.hivemq.ws.domain.Broker;

/**
 *
 */
public interface BrokerRepository {

	void save(String id, Broker broker);
	Broker find(String id);
	Broker remove(String id);
}
