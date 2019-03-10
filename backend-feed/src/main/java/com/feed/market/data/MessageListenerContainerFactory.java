package com.feed.market.data;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.PreDestroy;
import javax.jms.JMSException;
import javax.jms.Session;
import javax.jms.TopicConnection;
import javax.jms.TopicSession;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;


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
	
	Map<String, TopicConnection> myMapConnections = new ConcurrentHashMap<String,TopicConnection>();
	Map<String, javax.jms.TopicSession> myMapSessions = new ConcurrentHashMap<String,javax.jms.TopicSession>();

	
    @Autowired
    private ActiveMQConnectionFactory connectionFactory;

     public synchronized TopicConnection createTopicConnection(String topicName, String id) {
    	TopicConnection topicConnection = null;
    	String puSub = topicName + id;
		try {
			if(notContainsConnections(puSub)) {
				topicConnection = connectionFactory.createTopicConnection();
				topicConnection.setClientID(puSub);
				log.info(CREATE_CONNECTION_WITH_ID, puSub);
				myMapConnections.put(puSub, topicConnection);
				return topicConnection;
			}
		} catch (JMSException e) {
			log.error(ERROR_CREATING_CONNECTION_WITH_BROKER);
			log.error(e.getMessage());		
		}	
		
		return myMapConnections.get(puSub);
    }
    
    
    private boolean notContainsConnections(String topicName) {
		return !this.myMapConnections.containsKey(topicName);
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
 			if(notContainsSession(topicName) && Objects.nonNull(topicConnection)) {
				topicConsumerSession = topicConnection.createTopicSession(false, Session.AUTO_ACKNOWLEDGE);			
				myMapSessions.put(topicName, topicConsumerSession);
			}

		} catch (JMSException e) {
			log.error(ERROR_CREATING_CONSUMER_SESSION_TO_THE_BROKER);
			log.error(e.getMessage());
		}	
		return myMapSessions.get(topicName);
    }

    
	private boolean notContainsSession(String topicName) {
		return !myMapSessions.containsKey(topicName);
	}


	@PreDestroy
	public void initIt() throws Exception {
	  System.out.println("Close all connections");
	  myMapConnections.forEach((topicName, connection)-> {
		  try {
				connection.close();
			} catch (JMSException e) {
				log.error("Error closing connection to {}", topicName );
				log.error(e.getMessage());		
			}});
	  
	  myMapSessions.forEach((topicName, sessions)-> {
		  try {
			  sessions.close();
			} catch (JMSException e) {
				log.error("Error closing sessions to {}", topicName );
				log.error(e.getMessage());		
			}});
	  
	}
	
}
