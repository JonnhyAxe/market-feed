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

//http://activemq.apache.org/how-should-i-implement-request-response-with-jms.html

@Component
public class MessageListenerContainerFactory {

	private BrokerService broker;
    @Autowired
    private ActiveMQConnectionFactory connectionFactory;

    public MessageListenerContainerFactory() {
    }

//    @PostConstruct
//    public void initBrtoker() {
//		try {
//			broker = BrokerFactory.createBroker(new URI(
//					"broker:(tcp://localhost:61616)"));
//			broker.start();
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		
//    }
//    
//    @PreDestroy
//	public void cleanUp() throws Exception {
//    	if(!broker.isStopped()) {
//    		broker.stop();
//    	}
//    	
//	}
    
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
//    	TopicConnectionFactory connectionFactory = new ActiveMQConnectionFactory(
//				"tcp://localhost:61616");
    	TopicConnection topicConnection = null;

		try {
//			TopicConnectionFactory connectionFactory = new ActiveMQConnectionFactory(
//					"tcp://localhost:61616");
			topicConnection = connectionFactory.createTopicConnection();
			topicConnection.setClientID(id + UUID.randomUUID());
//			topicConsumerSession = topicConnection.createTopicSession(
//					false, Session.AUTO_ACKNOWLEDGE);			
//			Topic topic = topicConsumerSession.createTopic(topicName);
//			MessageConsumer consumer1 = topicConsumerSession.createSubscriber(topic);
//			consumer1.setMessageListener(new ConsumerMessageListener(
//					"Consumer1"));
//			topicConnection.start();

		} catch (JMSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		return topicConnection;

    }
    
    
    public TopicConnection removeTopicConnection(String topicName, String id) {
//    	TopicConnectionFactory connectionFactory = new ActiveMQConnectionFactory(
//				"tcp://localhost:61616");
    	TopicConnection topicConnection = null;

		try {
//			TopicConnectionFactory connectionFactory = new ActiveMQConnectionFactory(
//					"tcp://localhost:61616");
			topicConnection = connectionFactory.createTopicConnection();
			topicConnection.setClientID(id);
//			topicConsumerSession = topicConnection.createTopicSession(
//					false, Session.AUTO_ACKNOWLEDGE);			
//			Topic topic = topicConsumerSession.createTopic(topicName);
//			MessageConsumer consumer1 = topicConsumerSession.createSubscriber(topic);
//			consumer1.setMessageListener(new ConsumerMessageListener(
//					"Consumer1"));
//			topicConnection.start();

		} catch (JMSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		return topicConnection;

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
    
    @Value("${activemq.broker-url}")
    private String brokerUrl;

    
//    @Bean
//    public ActiveMQConnectionFactory activeMQConnectionFactory() {
//      ActiveMQConnectionFactory activeMQConnectionFactory =
//          new ActiveMQConnectionFactory();
//      activeMQConnectionFactory.setBrokerURL(brokerUrl);
//
//      return activeMQConnectionFactory;
//    }

    
}
