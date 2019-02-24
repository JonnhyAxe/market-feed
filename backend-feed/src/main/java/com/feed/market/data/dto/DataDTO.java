package com.feed.market.data.dto;

public class DataDTO {
	
	private String header; 
	private String body;
	
	public DataDTO() {
		super();
	}
	
	public DataDTO(String header, String body) {
		super();
		this.header = header;
		this.body = body;
	}
	
	public String getHeader() {
		return header;
	}
	public void setHeader(String header) {
		this.header = header;
	}
	public String getBody() {
		return body;
	}
	public void setBody(String body) {
		this.body = body;
	}

	@Override
	public String toString() {
		return "DataDTO [header=" + header + ", body=" + body + "]";
	}

}
