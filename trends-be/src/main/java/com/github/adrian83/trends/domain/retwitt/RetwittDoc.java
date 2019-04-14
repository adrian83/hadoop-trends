package com.github.adrian83.trends.domain.retwitt;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@JsonSerialize
@JsonDeserialize
@Document(collection = RetwittDoc.COLLECTION)
public class RetwittDoc {

	public static final String COLLECTION = "retwitts";

	public static final String ID = "id";
	public static final String TWITT_ID = "twitt_id";
	public static final String USERNAME = "username";
	public static final String UPDATED = "updated";
	public static final String RETWITT_COUNT = "count";

	@Id
	@Field(ID)
	private String documentId;
	@Field(TWITT_ID)
	private Long twittId;
	@Field(USERNAME)
	private String username;
	@Field(RETWITT_COUNT)
	private long count;
	@Field(UPDATED)
	private Long updated;

	public RetwittDoc(String documentId, Long twittId, String username, long count, Long updated) {
		super();
		this.documentId = documentId;
		this.twittId = twittId;
		this.username = username;
		this.count = count;
		this.updated = updated;
	}

	public String getDocumentId() {
		return documentId;
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
