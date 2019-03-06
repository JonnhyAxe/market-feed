package com.feed.market.data;

import java.util.List;

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
import com.feed.market.data.model.Data;
import com.feed.market.data.model.MarketData;
import com.feed.market.data.service.DataMessagingServiceImpl;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestScope
public class AmqpProducerController {
	
	private static final String MARKETS = "/markets";
	private static final String PUBLISHER_TOPIC_NAME = "/topic/{topicName}";
	private static final String SUBSCRIBER_TOPIC_NAME = "/topic/{topicName}";


	@Autowired
	private DataMapper dataMapper;
	

	@Autowired DataMessagingServiceImpl dataMessagingService;
	
	@GetMapping(path= MARKETS, produces = "application/json")
    public List<MarketData> getMarkets() {
		return dataMessagingService.getLAvailableMarkets();
    }
     
	
	   /**
     * send message to a given topic
     * @param topicName
     * @param payload
     * @return
     */
    @PostMapping(value = PUBLISHER_TOPIC_NAME)
    public Mono<ResponseEntity<?>> sendTypedMessageToTopic(@PathVariable String topicName, @RequestBody DataDTO payload) {
    	
		Data message = dataMapper.dtoToModel(payload);
		return dataMessagingService.sendTypedMessage(topicName, message);
		
    }
        
    @GetMapping(value = SUBSCRIBER_TOPIC_NAME, produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<?> receiveMessagesFromTopic(@PathVariable String topicName) {
    	return dataMessagingService.createMessageStream(topicName);

    }
    
}
