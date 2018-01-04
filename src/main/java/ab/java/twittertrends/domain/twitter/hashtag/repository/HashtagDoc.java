package ab.java.twittertrends.domain.twitter.hashtag.repository;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import ab.java.twittertrends.domain.twitter.common.TwitterDoc;

@Document(collection = HashtagDoc.HASHTAGS)
class HashtagDoc extends TwitterDoc {
	
	public static final String HASHTAGS = "hashtags";
	
	@Id
	private String id;
	private String name;
	private Long count;
	
	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public Long getCount() {
		return count;
	}
	
	public void setCount(Long count) {
		this.count = count;
	}

}