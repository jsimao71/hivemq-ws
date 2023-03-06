/**
 * 
 */
package com.hivemq.ws.util;

/**
 *
 */
public class MqttException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 */
	public MqttException() {
	}

	/**
	 * @param message
	 */
	public MqttException(String message) {
		super(message);
	}

	/**
	 * @param cause
	 */
	public MqttException(Throwable cause) {
		super(cause);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public MqttException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * @param message
	 * @param cause
	 * @param enableSuppression
	 * @param writableStackTrace
	 */
	public MqttException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
