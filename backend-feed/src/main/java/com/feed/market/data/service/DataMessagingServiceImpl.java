package com.feed.market.data.service;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

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
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.feed.market.data.MarketConfig;
import com.feed.market.data.MessageListenerContainerFactory;
import com.feed.market.data.model.Data;
import com.feed.market.data.model.MarketData;
import com.feed.market.data.producer.MessageProducer;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.ConnectableFlux;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;
import reactor.core.publisher.Mono;

@Slf4j
@Service
public class DataMessagingServiceImpl implements DataMessagingService {


    private static final String ERROR_CLOSING_CONSUMER = "Error closing consumer";
	private static final String ON_DISPOSE_TOPIC = "onDispose: topic={}";
	private static final String ERROR_SENDING_MESSAGE_TO_TOPIC = "Error sending message '{}' to topic '{}]'";
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
	
//	private Scheduler publisher = Schedulers.immediate();//newParallel("publisher", 50);
//	private Scheduler subscriber = Schedulers.immediate();// Schedulers.newParallel("subscriber", 50);

	@Autowired
    private MessageListenerContainerFactory messageListenerContainerFactory;

//	@Autowired 
//	private MessageProducer messageProducer;

	
	@Override
	public Mono<ResponseEntity<?>> sendTypedMessage(String topicName, Data message) {

		 TopicConnection mlc = messageListenerContainerFactory.createTopicConnection(topicName, PRODUCER);
	        TopicSession mlcSession = messageListenerContainerFactory.createTopicProducerConnectionSession(topicName, mlc);
			log.info(SENDING_TEXT_TO_TOPIC, message.getMessage(), topicName);
			
	        return Mono.fromCallable(() -> {
	        	Topic topic;
	    		Message msg;
	    		TopicPublisher publisher;
	    		try {
	    			msg = mlcSession.createTextMessage(message.getMessage());
	    			topic = mlcSession.createTopic(topicName);
	    			publisher = mlcSession.createPublisher(topic);
	    		    mlc.start();
	                publisher.publish(msg);
	    	        log.info(SEND_MESSAGE_TO_TOPIC, topicName);
	    	        
	    		} catch (JMSException e) {
	    			
	    			log.error(ERROR_SENDING_MESSAGE_TO_TOPIC, message.getMessage(), topicName);
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

	@Override
	public Flux<?> createMessageStream(String name) {
		javax.jms.TopicConnection topicConsumerConnection = messageListenerContainerFactory.createTopicConnection(name, CONSUMER);
        if(Objects.isNull(topicConsumerConnection)) {
        	   return Flux.empty();
                       
        }

        javax.jms.TopicSession topicConsumerSession = messageListenerContainerFactory.createTopicConsumerConnectionSession(name, topicConsumerConnection);
        Flux<String> fluxStream = null;
		try {
			Topic topic = topicConsumerSession.createTopic(name);
			MessageConsumer consumer = topicConsumerSession.createSubscriber(topic);
		
            fluxStream = Flux.<String> create(emitter -> {

            log.info(ADDING_LISTENER_TOPIC, name);
            try {
				consumer.setMessageListener((MessageListener) m -> {

				    log.debug(MESSAGE_RECEIVED_TOPIC, name);

				    if (emitter.isCancelled()) {
				        log.info(CANCELLED_TOPIC, name);
				        try {
							consumer.close();
						} catch (JMSException e) {
							 log.error(ERROR_CLOSING_CONSUMER);
						}
				        
				        return;
				    }
				    ActiveMQTextMessage msg =  (ActiveMQTextMessage)m; //Not cool?????
				    
					try {
						emitter.next(msg.getText());
						
//						log.info(MESSAGE_SENT_TO_CLIENT_TOPIC_TEXT, name, msg.getText());
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
            });
            
        
            log.info(STARTING_CONTAINER_TOPIC, name);
            try {
            	//more than on call when started is ignored.
				topicConsumerConnection.start();
			} catch (JMSException e) {
				log.error(e.getMessage());
			}
            log.info(CONTAINER_STARTED_TOPIC, name);
            
//        	Schedulers.elastic()?????

          });
          // .publishOn(Schedulers.elastic());//detache ActiveMQ onMessage
//        			.doOnNext(it -> log.info(": Publish : '{}'", Thread.currentThread().getName()))
//        			.subscribeOn(subscriber) // with client Push
//        			.doOnNext(it -> log.info(": Subscribe : '{}'", Thread.currentThread().getName()));
        
		} catch (JMSException e) {
			log.error(ERROR_CREATING_SESSION_BROKER_CONNECTION);
			log.error(e.getMessage());
		}
		
		return fluxStream;
	}


	@Override
	public List<MarketData> getLAvailableMarkets() {
		return MarketConfig.markets;
	}

}
