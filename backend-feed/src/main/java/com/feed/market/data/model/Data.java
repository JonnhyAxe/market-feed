package com.feed.market.data.model;

public class Data {
	
	private String message;
	
	public Data() {
		super();
	}
	
	public Data(String body) {
		super();
		this.message = body;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	@Override
	public String toString() {
		return "Data [message=" + message + "]";
	}

}
