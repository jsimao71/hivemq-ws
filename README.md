# hivemq-ws

HiveMQ WebService, HiveMQ simplified API, and HiveMQ WebService client.


# Packaging

## Package and Test

```
git clone https://github.com/jsimao71/hivemq-ws.git
cd hivemq-ws
mvn package
```

## Package Only

```
git clone https://github.com/jsimao71/hivemq-ws.git
cd hivemq-ws
mvn package -DskipTests
```

## Testing

### Test with Maven and JUnit

```
mvn test
```

Output:

```
....
[INFO]
[INFO] Results:
[INFO]
[INFO] Tests run: 9, Failures: 0, Errors: 0, Skipped: 0
[INFO]
[INFO]
[INFO] --- maven-jar-plugin:3.3.0:jar (default-jar) @ hivemq-ws ---
[INFO] Building jar: E:\git\hivemq\hivemq-ws\target\hivemq-ws-0.0.1-SNAPSHOT.jar
[INFO]
[INFO] --- spring-boot-maven-plugin:3.0.4:repackage (repackage) @ hivemq-ws ---
[INFO] Replacing main artifact with repackaged archive
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
[INFO] Total time: 02:17 min
[INFO] Finished at: 2023-03-09T19:52:37Z
[INFO] Final Memory: 28M/100M
[INFO] ------------------------------------------------------------------------
```

### Test with curl

+ Window 1: Setup broker:

```
cd hivemq-ws\src\test\resources
curl  -X PUT localhost:8080/mqtt/cloud -H "Content-Type: application/json" -d @broker.json
```

+ Window 2: Start receive with long pull:

```
curl localhost:8080/mqtt/cloud/receive/atopic
```

+ Window 1: Send 1+ messages:

```
curl -X POST localhost:8080/mqtt/cloud/send/atopic -H "Content-Type: application/json" -d @msg1.json
curl -X POST localhost:8080/mqtt/cloud/send/atopic -H "Content-Type: application/json" -d @msg2.json
curl -X POST localhost:8080/mqtt/cloud/send/atopic -H "Content-Type: application/json" -d @msg3.json
```

+ Window 2: Receive fixed number of messages

```
curl localhost:8080/mqtt/cloud/receive/atopic?n=3
```


## HiveMQTemplate

`HiveMQTemplate` provides a simplified API over the HiveMQ Java library.
The same API can be used to work with both HiveMQ v3 and v5 protocols.


### Defining Broker coordinates

```
String host = "xxxxxxxxxxxxxxx.s2.eu.hivemq.cloud";
String clientId = null;
String username = "myuser";
String password = "mypasswd";
String topic = "atopic";
int port=8883;
int websocketPort = 8884;

Broker broker = new Broker().withVersion(version)
    			.withHost(host).withPort(port).withWebsocketPort(websocketPort)
    			.withClient(clientId)
    			.withUsername(username).withPassword(password);
```

### Connecting to HiveMQ server

```
    			
HiveMQTemplate template = new HiveMQTemplate();
		
MqttClient client = template.connect(broker, (connAck, throwable) -> {
		if (throwable != null) {
			System.err.println("connectWith.whenComplete: " + throwable);
		} else {
				System.out.println("connectWith.whenComplete: " + connAck);
			}
		});
```

### Subscribing to Topic

```
template.subscribe(broker, client, topic, publish -> {
	ByteBuffer payload_ = null;
	if (broker.isVersion5()) {
		payload_ = ((Mqtt5Publish)publish).getPayload().get();
	} else if (broker.isVersion3()) {
		payload_ = ((Mqtt3Publish)publish).getPayload().get();		
	}
	String payload = UTF_8.decode(payload_).toString();   
	System.out.println("Rx: @" + topic + ": " + payload);
}, (subAck, throwable) -> {
    if (throwable != null) {
    	System.err.println("subscribeWith.whenComplete:" + throwable);
    } else {
    	System.out.println("subscribeWith.whenComplete:" + subAck);
    }
```

### Sending Messages

```
template.send(broker, client, topic, payload);

template.send(broker, topic, payload);
```


## HiveMQClient

`HiveMQClient` provide a API to consume the REST-API of HiveMQ webservice.

### Sending Messages

```
int port = 8080;
String uri = "http://localhost:" + port;

HiveMQWsClient client =  new HiveMQWsClient(uri);
String payload = "Hello HiveMQ";		
client.send(id, topic, payload);
```

### Receiving Messages

```
byte[] bytes = client.receive(id, topic, byte[].class);
String payload_ = new String(bytes);
System.out.println("client.receive:" + payload_);
```

## TODO / FutureWork

+ HTML/JS frontweb to test WebSockets
+ Test Dockerfile on Docker and Kubernetes
+ Refactor and test HiveMQClient.subscribe with Reactive/Streaming API
+ Integrate with HttpMessageConverter
+ Separate code in 3 different repositories: hivemq-template, hivemq-ws, hivemq-ws-client



