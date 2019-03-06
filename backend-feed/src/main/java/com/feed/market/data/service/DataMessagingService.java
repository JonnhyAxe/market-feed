package com.feed.market.data.service;

import org.springframework.http.ResponseEntity;

import com.feed.market.data.model.Data;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface DataMessagingService {

	
	Mono<ResponseEntity<?>> sendTypedMessage(String topicName, Data message);
	Flux<?> createMessageStream(String topicName);
}
