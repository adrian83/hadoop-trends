package com.github.adrian83.trends.domain.twitt;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;



@JsonSerialize
@JsonDeserialize
@Document(collection = TwittDoc.COLLECTION)
public class TwittDoc {

	
	
	public static final String COLLECTION = "twitts";
	
	public static final String ID = "id";
	public static final String TWITT_ID = "twitt_id";
	public static final String USERNAME = "username";
	public static final String UPDATED = "updated";
	public static final String FAVORITE_COUNT = "favorite_count";
	public static final String REPLY_COUNT = "reply_count";
	public static final String RETWITT_COUNT = "retwitt_count";
	
	
	
	@Id
	@Field(ID)
	private String documentId;
	@Field(TWITT_ID)
	private String twittId;
	@Field(USERNAME)
	private String username;
	@Field(FAVORITE_COUNT)
	private long favoriteCount;
	@Field(REPLY_COUNT)
	private long replyCount;
	@Field(RETWITT_COUNT)
	private long retwittCount;
	@Field(UPDATED)
	private Long updated;
	public TwittDoc(String documentId, String twittId, String username, long favoriteCount, long replyCount,
			long retwittCount, Long updated) {
		super();
		this.documentId = documentId;
		this.twittId = twittId;
		this.username = username;
		this.favoriteCount = favoriteCount;
		this.replyCount = replyCount;
		this.retwittCount = retwittCount;
		this.updated = updated;
	}
	
	public String getDocumentId() {
		return documentId;
	}
	
	public String getTwittId() {
		return twittId;
	}
	
	public String getUsername() {
		return username;
	}
	
	public long getFavoriteCount() {
		return favoriteCount;
	}
	
	public long getReplyCount() {
		return replyCount;
	}
	
	public long getRetwittCount() {
		return retwittCount;
	}
	
	public Long getUpdated() {
		return updated;
	}
	
	
	
}
