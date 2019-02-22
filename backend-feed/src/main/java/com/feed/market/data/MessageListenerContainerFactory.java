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
			topicConnection.setClientID(id);
			log.info("Create connection with id {}", randomId);


		} catch (JMSException e) {
			log.error("Error creating connection with broker");
			log.error(e.getMessage());		
		}	
		return topicConnection;

    }
    
    
    public void removeTopicConnection(TopicConnection topicConnection) {
		try {
			topicConnection.close();
		} catch (JMSException e) {
			log.error("Error closing the broker");
			log.error(e.getMessage());
		}	
    }
    
    
    public TopicSession createTopicProducerConnectionSession(String topicName, TopicConnection topicConnection) {
//    	TopicConnectionFactory connectionFactory = new ActiveMQConnectionFactory(
//				"tcp://localhost:61616");
//    	TopicConnection topicConnection = null;
    	TopicSession topicPublisherSession = null;
		try {
			topicPublisherSession = topicConnection.createTopicSession(false, Session.AUTO_ACKNOWLEDGE);
//			Topic topic = topicPublisherSession.createTopic(topicName);
//
//			Message msg = topicPublisherSession.createTextMessage(payload);
//			TopicPublisher publisher = topicPublisherSession.createPublisher(topic);
//			System.out.println("Sending text '" + payload + "'");
//			publisher.publish(msg);
		} catch (JMSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		return topicPublisherSession;

    }
    
    public TopicSession createTopicConsumerConnectionSession(String topicName, TopicConnection topicConnection) {
//    	TopicConnectionFactory connectionFactory = new ActiveMQConnectionFactory(
//				"tcp://localhost:61616");
//    	TopicConnection topicConnection = null;
    	TopicSession topicConsumerSession = null;
		try {
			topicConsumerSession = topicConnection.createTopicSession(
					false, Session.AUTO_ACKNOWLEDGE);			
//			Topic topic = topicConsumerSession.createTopic(topicName);
//			MessageConsumer consumer1 = topicConsumerSession.createSubscriber(topic);
//			consumer1.setMessageListener(new ConsumerMessageListener(
//					"Consumer1"));
//			topicConnection.start();

		} catch (JMSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		return topicConsumerSession;

    }


    
}
