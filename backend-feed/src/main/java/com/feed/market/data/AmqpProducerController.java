package com.feed.market.data;

import java.time.Duration;
import java.util.List;
import java.util.Objects;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.Topic;
import javax.jms.TopicConnection;
import javax.jms.TopicPublisher;
import javax.jms.TopicSession;

import org.apache.activemq.command.ActiveMQTextMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.annotation.RequestScope;

import com.feed.market.data.dto.DataDTO;
import com.feed.market.data.dto.mapper.DataMapper;
import com.feed.market.data.mockeserver.MessageProducer;
import com.feed.market.data.model.Data;
import com.feed.market.data.model.MarketData;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
@RequestScope
public class AmqpProducerController {

    private static final String ON_DISPOSE_TOPIC = "onDispose: topic={}";
	private static final String HEARTBEAT_MSG = "heartbeat...";
	private static final String ERROR_SENDING_MESSAGE_TO_TOPIC = "Error sending message '{}' to topic '{}]'";
	private static final String SUBSCRIBER_TOPIC_NAME = "/topic/{name}";
	private static final String PUBLISHER_TOPIC_NAME = "/topic/{topicName}";
	private static final String SEND_MESSAGE_TO_TOPIC = "Send Message To Topic '{}'";
	private static final String SENDING_TEXT_TO_TOPIC = "Sending text '{}' to topic '{}'";
	private static final String PRODUCER = "Producer";
	private static final String CONSUMER= "Consumer";
	private static final String MESSAGE_RECEIVED_TOPIC = "Message received, topic={}";
	private static final String ADDING_LISTENER_TOPIC = "Adding listener, topic={}";
	private static final String CANCELLED_TOPIC = "Cancelled, topic={}";
	private static final String MESSAGE_SENT_TO_CLIENT_TOPIC_TEXT = "Message sent to client, topic={}, text={}";
	private static final String ERROR_SENDING_MESSAGE_TO_CLIENT = "Error sending message to client";
	private static final String ERROR_CLOSING_BROKER_CONNECTION = "Error closing broker connection";
	private static final String STARTING_CONTAINER_TOPIC = "Starting container, topic={}";
	private static final String CONTAINER_STARTED_TOPIC = "Container started, topic={}";
	private static final String ERROR_CREATING_SESSION_BROKER_CONNECTION = "Error creating session broker connection";
	private static final String SENDING_HEARTBEAT = "Sending heartbeat...";
	

	
	@Autowired
    private MessageListenerContainerFactory messageListenerContainerFactory;

	@Autowired
	private DataMapper dataMapper;
	
	@Autowired MessageProducer messageProducer;
	
	@GetMapping(path="/markets", produces = "application/json")
    public List<MarketData> getMarkets() {
        return MarketConfig.markets;
    }
     
	
	   /**
     * send message to a given topic
     * @param topicName
     * @param payload
     * @return
     */
    @PostMapping(value = PUBLISHER_TOPIC_NAME)
    public Mono<ResponseEntity<?>> sendTypedMessageToTopic(@PathVariable String topicName, @RequestBody DataDTO payload) {
    	
        TopicConnection mlc = messageListenerContainerFactory.createTopicConnection(topicName, PRODUCER);
        TopicSession mlcSession = messageListenerContainerFactory.createTopicProducerConnectionSession(topicName, mlc);
		log.info(SENDING_TEXT_TO_TOPIC, payload, topicName);
		
        return Mono.fromCallable(() -> {
        	Topic topic;
    		Message msg;
    		TopicPublisher publisher;
    		try {
    			Data message = dataMapper.dtoToModel(payload);
    			msg = mlcSession.createTextMessage(message.getMessage());
    			topic = mlcSession.createTopic(topicName);
    			publisher = mlcSession.createPublisher(topic);
    		    mlc.start();
                publisher.publish(msg);
    	        log.info(SEND_MESSAGE_TO_TOPIC, topicName);
    	        
    		} catch (JMSException e) {
    			
    			log.error(ERROR_SENDING_MESSAGE_TO_TOPIC, payload, topicName);
				log.error(e.getMessage());
				return ResponseEntity.badRequest()
			                .build();
    		} finally {
    			mlcSession.close();
    			mlc.stop();
    		}
    		
            return ResponseEntity.accepted()
                .build();
        });
    }
        
    @GetMapping(value = SUBSCRIBER_TOPIC_NAME, produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<?> receiveMessagesFromTopic(@PathVariable String name) {

    	javax.jms.TopicConnection topicConsumerConnection = messageListenerContainerFactory.createTopicConnection(name, CONSUMER);
        if(Objects.isNull(topicConsumerConnection)) {
        	   return Flux.empty();
                       
        }
    	
        javax.jms.TopicSession topicConsumerSession = messageListenerContainerFactory.createTopicConsumerConnectionSession(name, topicConsumerConnection);
        Flux<String> f = null;
		try {
			Topic topic = topicConsumerSession.createTopic(name);
			MessageConsumer consumer = topicConsumerSession.createSubscriber(topic);
		
            f = Flux.<String> create(emitter -> {

            log.info(ADDING_LISTENER_TOPIC, name);
            try {
				consumer.setMessageListener((MessageListener) m -> {

				    log.info(MESSAGE_RECEIVED_TOPIC, name);

				    if (emitter.isCancelled()) {
				        log.info(CANCELLED_TOPIC, name);
				        try {
							topicConsumerConnection.stop();
							topicConsumerSession.close();
						} catch (JMSException e) {
							log.error(ERROR_CLOSING_BROKER_CONNECTION);
							log.error(e.getMessage());
						}
				        return;
				    }
				    ActiveMQTextMessage msg =  (ActiveMQTextMessage)m; //Not cool?????
				    
//				    String payload ;ActiveMQMessage aMsg =  (ActiveMQMessage)m;
					try {
//						payload = new String(m.getBody(String.class)); 
						emitter.next(msg.getText());
						
						log.info(MESSAGE_SENT_TO_CLIENT_TOPIC_TEXT, name, msg.getText());
					} catch (JMSException e) {
						log.error(ERROR_SENDING_MESSAGE_TO_CLIENT);
						log.error(e.getMessage());
					}


				});
			} catch (JMSException e) {
				log.error(ERROR_CLOSING_BROKER_CONNECTION);
				log.error(e.getMessage());
			}

//            emitter.onRequest(v -> {
//                log.info(" emitter.onRequest", name);
//            });

            emitter.onDispose(() -> {
                log.info(ON_DISPOSE_TOPIC, name);
                try {
					topicConsumerConnection.stop();
					topicConsumerSession.close();
					
				} catch (JMSException e) {
					log.error(e.getMessage());
				}
            });
            
        
            log.info(STARTING_CONTAINER_TOPIC, name);
            try {
				topicConsumerConnection.start();
			} catch (JMSException e) {
				log.error(e.getMessage());
			}
            log.info(CONTAINER_STARTED_TOPIC, name);

          });
        
		} catch (JMSException e) {
			log.error(ERROR_CREATING_SESSION_BROKER_CONNECTION);
			log.error(e.getMessage());
		}
        
        return Flux.interval(Duration.ofSeconds(30))
          .map(v -> {
                log.info(SENDING_HEARTBEAT);
                return HEARTBEAT_MSG;
          })
          .mergeWith(f);

    }
}
