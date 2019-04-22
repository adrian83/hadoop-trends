package com.github.adrian83.trends.domain.retwitt;

public class Retwitt {

	private String twittId;
	private String username;
	private long count;
	
	public Retwitt(String twittId, String username, long count) {
		super();
		this.twittId = twittId;
		this.username = username;
		this.count = count;
	}

	public String getTwittId() {
		return twittId;
	}

	public String getUsername() {
		return username;
	}

	public long getCount() {
		return count;
	}
	
	
	
}