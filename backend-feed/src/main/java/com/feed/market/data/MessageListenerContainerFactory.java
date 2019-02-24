package com.feed.market.data;

import java.util.UUID;

import javax.jms.JMSException;
import javax.jms.Session;
import javax.jms.TopicConnection;
import javax.jms.TopicSession;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.broker.BrokerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

//http://activemq.apache.org/how-should-i-implement-request-response-with-jms.html

@Slf4j
@Component
public class MessageListenerContainerFactory {

    private static final String CREATE_CONNECTION_WITH_ID = "Create connection with id {}";
	private static final String ERROR_CREATING_CONNECTION_WITH_BROKER = "Error creating connection with broker";
	private static final String ERROR_CLOSING_THE_BROKER = "Error closing the broker";
	private static final String ERROR_CREATING_PRODUCER_SESSION_TO_THE_BROKER = "Error creating producer session to the broker";
	private static final String ERROR_CREATING_CONSUMER_SESSION_TO_THE_BROKER = "Error creating consumer session to the broker";


	@Value("${activemq.broker-url}")
    private String brokerUrl;

	
    @Autowired
    private ActiveMQConnectionFactory connectionFactory;

    
//    public MessageListenerContainer createMessageListenerContainer(String queueName) {
//    	
////        https://www.baeldung.com/spring-remoting-jms
//        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
//        container.setConnectionFactory(connectionFactory);
//        container.setDestinationName(queueName);
//        container.setConcurrentConsumers(1);
//        container.setDestination(new ActiveMQTopic(queueName));
//
//        return container;
//    }

    
    public TopicConnection createTopicConnection(String topicName, String id) {
    	TopicConnection topicConnection = null;
		try {
			topicConnection = connectionFactory.createTopicConnection();
			String randomId = id + UUID.randomUUID();
			topicConnection.setClientID(randomId);
			log.info(CREATE_CONNECTION_WITH_ID, randomId);


		} catch (JMSException e) {
			log.error(ERROR_CREATING_CONNECTION_WITH_BROKER);
			log.error(e.getMessage());		
		}	
		return topicConnection;
    }
    
    
    public void removeTopicConnection(TopicConnection topicConnection) {
		try {
			topicConnection.close();
		} catch (JMSException e) {
			log.error(ERROR_CLOSING_THE_BROKER);
			log.error(e.getMessage());
		}	
    }
    
    
    public TopicSession createTopicProducerConnectionSession(String topicName, TopicConnection topicConnection) {

    	TopicSession topicPublisherSession = null;
		try {
			topicPublisherSession = topicConnection.createTopicSession(false, Session.AUTO_ACKNOWLEDGE);
		} catch (JMSException e) {
			log.error(ERROR_CREATING_PRODUCER_SESSION_TO_THE_BROKER);
			log.error(e.getMessage());
		}	
		return topicPublisherSession;

    }
    
    public TopicSession createTopicConsumerConnectionSession(String topicName, TopicConnection topicConnection) {
    	TopicSession topicConsumerSession = null;
		try {
			topicConsumerSession = topicConnection.createTopicSession(false, Session.AUTO_ACKNOWLEDGE);			

		} catch (JMSException e) {
			log.error(ERROR_CREATING_CONSUMER_SESSION_TO_THE_BROKER);
			log.error(e.getMessage());
		}	
		return topicConsumerSession;
    }

}
