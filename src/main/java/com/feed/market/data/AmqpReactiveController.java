package com.feed.market.data;

import java.time.Duration;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.Topic;
import javax.jms.TopicConnection;
import javax.jms.TopicPublisher;
import javax.jms.TopicSession;

import org.apache.activemq.command.ActiveMQTextMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
public class AmqpReactiveController {

    private static Logger log = LoggerFactory.getLogger(AmqpReactiveController.class);

    @Autowired
    private MessageListenerContainerFactory messageListenerContainerFactory;


    /**
     * send message to a given topic
     * @param topicName
     * @param payload
     * @return
     */
    @PostMapping(value = "/topic/{topicName}")
    public Mono<ResponseEntity<?>> sendMessageToTopic(@PathVariable String topicName, @RequestBody String payload) {

        TopicConnection mlc = messageListenerContainerFactory.createTopicConnection(topicName, "Producer" );
        TopicSession mlcSession = messageListenerContainerFactory.createTopicProducerConnectionSession(topicName, mlc);

		

		System.out.println("Sending text '" + payload + "'");
		
		
        return Mono.fromCallable(() -> {
        	Topic topic = null;
    		Message msg = null;
    		TopicPublisher publisher = null;
    		try {
    			msg = mlcSession.createTextMessage(payload);
    			topic = mlcSession.createTopic(topicName);
    			publisher = mlcSession.createPublisher(topic);
    		} catch (JMSException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		}
            log.info("[I51] sendMessageToTopic");
            mlc.start();
            publisher.publish(msg);
            mlcSession.close();
            mlc.stop();
            
            return ResponseEntity.accepted()
                .build();

        });
       
    }

    @GetMapping(value = "/topic/{name}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<?> receiveMessagesFromTopic(@PathVariable String name) {

    	javax.jms.TopicConnection topicConsumerConnection = messageListenerContainerFactory.createTopicConnection(name, "Consumer");
        javax.jms.TopicSession topicConsumerSession = messageListenerContainerFactory.createTopicConsumerConnectionSession(name, topicConsumerConnection);
        Topic topic;
        
        Flux<String> f = null;
		try {
			topic = topicConsumerSession.createTopic(name);
			MessageConsumer consumer1 = topicConsumerSession.createSubscriber(topic);
		
            f = Flux.<String> create(emitter -> {

            log.info("[I168] Adding listener, queue={}", name);
            try {
				consumer1.setMessageListener((MessageListener) m -> {

				    log.info("[I137] Message received, queue={}", name);

				    if (emitter.isCancelled()) {
				        log.info("[I166] cancelled, queue={}", name);
				        try {
							topicConsumerConnection.stop();
							topicConsumerSession.close();
						} catch (JMSException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
				        return;
				    }
				    ActiveMQTextMessage msg =  (ActiveMQTextMessage)m;
//				    String payload ;ActiveMQMessage aMsg =  (ActiveMQMessage)m;
					try {
//						payload = new String(m.getBody(String.class)); 
						emitter.next(msg.getText());
						
						log.info("[I176] Message sent to client, queue={}, text={}", name, msg.getText());
					} catch (JMSException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}


				});
			} catch (JMSException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

            emitter.onRequest(v -> {
                log.info("[I171] Starting container, queue={}", name);
                try {
					topicConsumerConnection.start();
				} catch (JMSException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            });

            emitter.onDispose(() -> {
                log.info("[I176] onDispose: queue={}", name);
//                amqpAdmin.deleteQueue(qname);
                try {
					topicConsumerConnection.stop();
					topicConsumerSession.close();
					
				} catch (JMSException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            });            

            log.info("[I171] Container started, queue={}", name);

          });
        
		} catch (JMSException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
        
        return Flux.interval(Duration.ofSeconds(5))
          .map(v -> {
                log.info("[I209] Seding heartbeat...");
                return "heartbeat...";
          })
          .mergeWith(f);

    }

}
