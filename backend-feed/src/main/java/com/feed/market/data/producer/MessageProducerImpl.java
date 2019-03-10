package com.feed.market.data.producer;

import java.util.Date;
import java.util.List;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Topic;
import javax.jms.TopicConnection;
import javax.jms.TopicPublisher;
import javax.jms.TopicSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.feed.market.data.MarketConfig;
import com.feed.market.data.MessageListenerContainerFactory;
import com.feed.market.data.model.MarketData;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class MessageProducerImpl implements MessageProducer {
	
	private static final int START_NOW = 0;

	private static final String PRODUCER = "Producer";

	private static final String SENDING_TEXT_TO_TOPIC = "Sending text '{}' to topic '{}'";

	private static List<MarketData> marketsInfo  = MarketConfig.markets;

	private ScheduledExecutorService executor;
	
	private ObjectMapper mapper = new ObjectMapper();
	
	@Autowired
    private MessageListenerContainerFactory messageListenerContainerFactory;

	private TopicConnection mlc ;
    private TopicSession mlcSession ;
    private Topic topic;
    private TopicPublisher publisher;
	
	public Boolean startProducing(String topicName) {

		try {

			mlc = messageListenerContainerFactory.createTopicConnection(topicName, PRODUCER);
		    mlcSession = messageListenerContainerFactory.createTopicProducerConnectionSession(topicName, mlc);
			topic = mlcSession.createTopic(topicName);
			publisher = mlcSession.createPublisher(topic);
		    mlc.start();
         
	        log.info("Publisher created for topic '{}'", topicName, topic);
	        
		} catch (JMSException e) {
			
			log.error("Error creating publisher created for topic '{}'", topicName);
			log.error(e.getMessage());
			return Boolean.FALSE;
		} 
	   this.executor =  Executors.newSingleThreadScheduledExecutor();
       log.info("Starting streaming with '{}'", topicName);
       executor.scheduleAtFixedRate(RANDOM_MARKET_CHANGE_TASK, START_NOW, 30, TimeUnit.MICROSECONDS);

       return Boolean.TRUE;
	}

	@Override
	public Boolean stopProducing(String topicName) {
		 log.info("Stop streaming for topic'{}'", topicName);
		 
		 try {
			mlcSession.close();
			mlc.stop();
			
		} catch (JMSException e) {
			return Boolean.FALSE;
		}
		 if(!executor.isShutdown()) {
			 executor.shutdown(); 
		 }
		
		return Boolean.TRUE;
	}
	
	 public int getRandomMarket() {
		 return  (int) Math.floor(marketsInfo.size() * Math.random()); 
//		 return ThreadLocalRandom.current().nextInt(0, markets);
	 }
	 
	 
	 public MarketData makeSomeChanges() {
		 int index = getRandomMarket();
		 log.debug("Get data from index '{}'", index);

		 MarketData dataItem = marketsInfo.get(index);
	  
		 Double move = Math.floor(30 * Math.random()) / 10 - 1;
	     Double newValue = dataItem.getMid() + move;
	     dataItem.setMid(newValue);
	     dataItem.setBid(newValue * 0.98);
	     dataItem.setAsk(newValue * 1.02);
	     
	     dataItem.setVolume(Math.floor(Math.random() * 10000 + 100));
	     log.debug("Data changed  '{}'", dataItem);
	     
//	     return dataItem.toBuilder().build(); 
	     return dataItem; 

	 }
	 
	private TimerTask RANDOM_MARKET_CHANGE_TASK = new TimerTask() {
	    public void run() {
	    	sendMArketDataChnaged();
	    }
	};
	      
	public void sendMArketDataChnaged() {
		log.debug("Task publish on '{}'", new Date());
    	MarketData marketMove =  makeSomeChanges();
    	
    	try {
      		String payload = mapper.writeValueAsString(marketMove);
    		Message msg = mlcSession.createTextMessage(payload);
			publisher.publish(msg);
        	log.debug(SENDING_TEXT_TO_TOPIC, payload, topic);
        	
    	} catch (Exception e) {
			log.error("Error publishing string from MarketData '{}'", marketMove);
			return;
		}
    	
	}
}
