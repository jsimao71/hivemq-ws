# hivemq-ws
HiveMQ WebService.


## Test

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