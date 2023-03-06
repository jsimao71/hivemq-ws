package com.hivemq.ws.domain;

public class Broker {

	public static final String MQTT_VERSION_3 = "3";
	public static final String MQTT_VERSION_5 = "5";
	
	public static final String MQTT_DEFAULT_VERSION = MQTT_VERSION_5;
	
	private String version = MQTT_DEFAULT_VERSION;	
	private String host;
	private String client;
	private String username;
	private String password;
	private String topic;
	private int port=8883;
	private int websocketPort = 8884;

	/**
	 * 
	 */
	public Broker() {
	}
	
	/**
	 * @return the version
	 */
	public String getVersion() {
		return version;
	}

	/**
	 * @param version the version to set
	 */
	public void setVersion(String version) {
		this.version = version;
	}

	/**
	 * @return the host
	 */
	public String getHost() {
		return host;
	}

	/**
	 * @param host the host to set
	 */
	public void setHost(String host) {
		this.host = host;
	}

	/**
	 * @return the client
	 */
	public String getClient() {
		return client;
	}

	/**
	 * @param client the client to set
	 */
	public void setClient(String client) {
		this.client = client;
	}

	/**
	 * @return the username
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * @param username the username to set
	 */
	public void setUsername(String username) {
		this.username = username;
	}

	/**
	 * @return the password
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * @param password the password to set
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * @return the topic
	 */
	public String getTopic() {
		return topic;
	}

	/**
	 * @param topic the topic to set
	 */
	public void setTopic(String topic) {
		this.topic = topic;
	}

	/**
	 * @return the port
	 */
	public int getPort() {
		return port;
	}

	/**
	 * @param port the port to set
	 */
	public void setPort(int port) {
		this.port = port;
	}

	/**
	 * @return the websocketPort
	 */
	public int getWebsocketPort() {
		return websocketPort;
	}

	/**
	 * @param websocketPort the websocketPort to set
	 */
	public void setWebsocketPort(int websocketPort) {
		this.websocketPort = websocketPort;
	}
	

	/**
	 * @param version the version to set
	 */
	public Broker withVersion(String version) {
		this.version = version;
		return this;
	}


	/**
	 * @param host the host to set
	 */
	public Broker withHost(String host) {
		this.host = host;
		return this;
	}


	/**
	 * @param client the client to set
	 */
	public Broker withClient(String client) {
		this.client = client;
		return this;
	}

	/**
	 * @param username the username to set
	 */
	public Broker withUsername(String username) {
		this.username = username;
		return this;
	}

	/**
	 * @param password the password to with
	 */
	public Broker withPassword(String password) {
		this.password = password;
		return this;
	}

	/**
	 * @param topic the topic to with
	 */
	public Broker withTopic(String topic) {
		this.topic = topic;
		return this;
	}

	/**
	 * @param port the port to with
	 */
	public Broker withPort(int port) {
		this.port = port;
		return this;
	}

	/**
	 * @param websocketPort the websocketPort to with
	 */
	public Broker withWebsocketPort(int websocketPort) {
		this.websocketPort = websocketPort;
		return this;
	}

	@Override
	public String toString() {
		return "Broker [version=" + version + ", host=" + host + ", client=" + client + ", username=" + username
				+ ", password=" + password + ", topic=" + topic + ", port=" + port + ", websocketPort="
				+ websocketPort + "]";
	}

	public boolean isVersion5() {
		return MQTT_VERSION_5.equals(version);
	}

	public boolean isVersion3() {
		return MQTT_VERSION_3.equals(version);
	}

	
}
