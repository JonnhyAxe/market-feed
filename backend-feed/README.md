# spring-jms-rx-activemq

0. mvn --projects backend-feed spring-boot:run
1. http://localhost:8080/index.html#/ (this subscribes to http://localhost:8080/topic/price-updates)
2. http://localhost:8080/swagger-ui.html#!/amqp-reactive-controller/startProducerUsingPOST and topicName as price-updates 
3. http://localhost:8080/swagger-ui.html#!/amqp-reactive-controller/stopProducerUsingPOST and topicName as price-updates 



curl -v -H "Accept: text/event-stream" http://localhost:8080/topic/price-updates

curl -X POST http://localhost:8080/topic/WEATHER --data hello

http://localhost:8080/swagger-ui.html#!/amqp-reactive-controller/sendMessageToTopicUsingPOST

https://github.com/swagger-api/swagger-codegen

java -jar swagger-codegen-cli-2.3.1.jar generate -i http://localhost:8080/v2/api-docs -l java -o src/main/java/com/swagger/


https://howtodoinjava.com/swagger2/code-generation-for-rest-api/
https://www.baeldung.com/spring-boot-rest-client-swagger-codegen

https://howtodoinjava.com/swagger2/code-generation-for-rest-api/
https://github.com/OpenAPITools/openapi-generator
https://github.com/OpenAPITools/openapi-generator/blob/master/modules/openapi-generator/src/main/java/org/openapitools/codegen/languages/SpringCodegen.java

https://mucahit.io/2018/08/27/instrumenting-and-monitoring-spring-boot-2-applications/
