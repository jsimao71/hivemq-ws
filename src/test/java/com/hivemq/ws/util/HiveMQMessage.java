/**
 * 
 */
package com.hivemq.ws.util;

/**
 *
 */
public class HiveMQMessage<T> {
	public T payload;
	
	/**
	 * 
	 */
	public HiveMQMessage() {
	}
	/**
	 * 
	 */
	public HiveMQMessage(T payload) {
		this.payload = payload;
	}
	/**
	 * @return the payload
	 */
	public T getPayload() {
		return payload;
	}
	/**
	 * @param payload the payload to set
	 */
	public void setPayload(T payload) {
		this.payload = payload;
	}
	@Override
	public String toString() {
		return "HiveMQMessage [payload=" + payload + "]";
	}
	

	

}