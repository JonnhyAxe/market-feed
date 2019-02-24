# spring-jms-rx-activemq


http://localhost:8080/swagger-ui.html#/

curl -v -H "Accept: text/event-stream" http://localhost:8080/topic/price-updates

curl -X POST http://localhost:8080/topic/WEATHER --data hello

http://localhost:8080/swagger-ui.html#!/amqp-reactive-controller/sendMessageToTopicUsingPOST

java -jar swagger-codegen-cli-2.3.1.jar generate -i http://localhost:8080/v2/api-docs -l java -o ./tmp