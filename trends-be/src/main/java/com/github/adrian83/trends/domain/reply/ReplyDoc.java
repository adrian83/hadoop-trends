package com.github.adrian83.trends.domain.reply;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@JsonSerialize
@JsonDeserialize
@Document(collection = ReplyDoc.COLLECTION)
public class ReplyDoc {

	public static final String COLLECTION = "replies";

	public static final String ID = "id";
	public static final String TWITT_ID = "twitt_id";
	public static final String USERNAME = "username";
	public static final String UPDATED = "updated";
	public static final String REPLY_COUNT = "count";

	@Id
	@Field(ID)
	private String documentId;
	@Field(TWITT_ID)
	private Long twittId;
	@Field(USERNAME)
	private String username;
	@Field(REPLY_COUNT)
	private long count;
	@Field(UPDATED)
	private Long updated;

	public ReplyDoc(String documentId, Long twittId, String username, long count, Long updated) {
		super();
		this.twittId = twittId;
		this.username = username;
		this.count = count;
		this.updated = updated;
	}

	public Long getTwittId() {
		return twittId;
	}

	public String getUsername() {
		return username;
	}
	
	public long getCount() {
		return count;
	}

	public Long getUpdated() {
		return updated;
	}

}
