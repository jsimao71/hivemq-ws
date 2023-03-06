/**
 * 
 */
package com.hivemq.ws.util;

/**
 *
 */
public class MqttVersionException extends MqttException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 */
	public MqttVersionException() {
	}

	/**
	 * @param message
	 */
	public MqttVersionException(String message) {
		super(message);
	}

	/**
	 * @param cause
	 */
	public MqttVersionException(Throwable cause) {
		super(cause);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public MqttVersionException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * @param message
	 * @param cause
	 * @param enableSuppression
	 * @param writableStackTrace
	 */
	public MqttVersionException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
