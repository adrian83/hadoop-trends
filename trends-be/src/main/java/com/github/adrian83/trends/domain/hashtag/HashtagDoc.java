package com.github.adrian83.trends.domain.hashtag;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@JsonSerialize
@JsonDeserialize
@Document(collection = HashtagDoc.COLLECTION)
public class HashtagDoc {
	
	public static final String COLLECTION = "hashtags";

	public static final String ID = "id";
	public static final String UPDATED = "updated";
	public static final String NAME = "name";
	public static final String OCCURRENCE_COUNT = "occurrence_count";

	@Id
	@Field(ID)
	private String documentId;
	@Field(NAME)
	private String name;
	@Field(OCCURRENCE_COUNT)
	private long occurrenceCount;
	@Field(UPDATED)
	private Long updated;

	public HashtagDoc(String documentId, String name, long occurrenceCount, Long updated) {
		super();
		this.documentId = documentId;
		this.name = name;
		this.occurrenceCount = occurrenceCount;
		this.updated = updated;
	}

	public String getDocumentId() {
		return documentId;
	}

	public String getName() {
		return name;
	}

	public long getOccurrenceCount() {
		return occurrenceCount;
	}

	public Long getUpdated() {
		return updated;
	}

}
