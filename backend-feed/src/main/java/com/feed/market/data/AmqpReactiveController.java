package com.feed.market.data;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.feed.market.data.mockeserver.MessageProducer;


import reactor.core.publisher.Mono;

@RestController
public class AmqpReactiveController {
	
	@Autowired MessageProducer messageProducer;
	    
    @PostMapping(value = "/start-publishing/{topicName}")
    public Mono<ResponseEntity<?>> startProducer(@PathVariable String topicName) {

        return Mono.fromCallable(() -> {
        	if(messageProducer.startProducing(topicName)) {
        		 return ResponseEntity.accepted().build();
        	} 
        	return ResponseEntity.badRequest().build();
        	
        });
    }
    
    
    @PostMapping(value = "/stop-publishing/{topicName}")
    public Mono<ResponseEntity<?>> stopProducer(@PathVariable String topicName) {
    	
    	  return Mono.fromCallable(() -> {
          	if(messageProducer.stopProducing(topicName)) {
          		 return ResponseEntity.accepted() .build();
          	} 
          	return ResponseEntity.badRequest().build();
          	
          });
    }
}
