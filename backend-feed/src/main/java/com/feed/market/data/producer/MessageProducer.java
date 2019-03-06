package com.feed.market.data.producer;

public interface MessageProducer {
	public Boolean startProducing(String topicName);
	public Boolean stopProducing(String topicName);
}
