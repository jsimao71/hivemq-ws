/**
 * 
 */
package com.hivemq.ws.config;

import java.util.Arrays;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

import com.hivemq.ws.dao.BrokerRepository;
import com.hivemq.ws.dao.SimpleBrokerRepository;
import com.hivemq.ws.web.OctetStreamHttpMessageConverter;

/**
 *
 */
@Configuration
public class AppConfig {

	@Bean
	public BrokerRepository brokerRepository() {
		return new SimpleBrokerRepository();
	}

	//@Bean
	public HttpMessageConverter<?> converter() {
		MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
	    converter.setSupportedMediaTypes(
	            Arrays.asList(new MediaType[]{
	            		MediaType.APPLICATION_JSON,
	            		MediaType.APPLICATION_OCTET_STREAM,
	            		MediaType.parseMediaType("text/plain;charset=utf-8"),
	            		}));
	    return converter;
	}
	
	@Bean
	public HttpMessageConverter<?> converter2() {
		return new OctetStreamHttpMessageConverter();
	}

}
