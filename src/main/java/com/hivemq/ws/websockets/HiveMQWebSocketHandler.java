/**
 * 
 */
package com.hivemq.ws.websockets;


import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;


public class HiveMQWebSocketHandler  extends TextWebSocketHandler {

	private final Log logger = LogFactory.getLog(getClass());

    @Override
    public void handleTextMessage(WebSocketSession session, final TextMessage message) {
		logger.info("handleTextMessage:" + message + " " + session);
		TextMessage message2 = new TextMessage("Reply:" + message.getPayload());
		try {
			session.sendMessage(message2);
		} catch (IOException e) {
			logger.error("handleTextMessage:" + e.getMessage());
		}
    }
}
