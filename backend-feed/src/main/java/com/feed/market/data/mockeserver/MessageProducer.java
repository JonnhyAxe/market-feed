package com.feed.market.data.mockeserver;

public interface MessageProducer {
	public Boolean startProducing(String topicName);
	public Boolean stopProducing(String topicName);
}
