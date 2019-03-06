package com.feed.market.data.service;

import java.util.List;

import org.springframework.http.ResponseEntity;

import com.feed.market.data.model.Data;
import com.feed.market.data.model.MarketData;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface DataMessagingService {
	
	List<MarketData> getLAvailableMarkets();
	
	Mono<ResponseEntity<?>> sendTypedMessage(String topicName, Data message);
	
	Flux<?> createMessageStream(String topicName);
}
