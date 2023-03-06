/**
 * 
 */
package com.hivemq.ws.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.hivemq.ws.dao.BrokerRepository;
import com.hivemq.ws.dao.SimpleBrokerRepository;

/**
 *
 */
@Configuration
public class AppConfig {

	@Bean
	public BrokerRepository brokerRepository() {
		return new SimpleBrokerRepository();
	}
}
