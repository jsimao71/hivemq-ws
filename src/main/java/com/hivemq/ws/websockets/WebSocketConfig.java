/**
 * 
 */
package com.hivemq.ws.websockets;

import org.springframework.context.annotation.Bean;

import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

//@Configuration
//@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(handler(), "/ws").setAllowedOrigins(/*"*.einnovator.org",*/ "*").withSockJS();
    }

    @Bean
    public HiveMQWebSocketHandler handler() {
        return new HiveMQWebSocketHandler();
    }

}