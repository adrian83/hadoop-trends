package ab.java.twittertrends.domain.twitter.retwitt.repository;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = RetwittDoc.RETWITTS)
public class RetwittDoc {
	
	public static final String RETWITTS = "retwitts";

	@Id
	private String id;
	private Long twittId;
	private Integer retwitted;
	private String user;
	
	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	public Long getTwittId() {
		return twittId;
	}
	
	public void setTwittId(Long twittId) {
		this.twittId = twittId;
	}
	
	public Integer getRetwitted() {
		return retwitted;
	}
	
	public void setRetwitted(Integer retwitted) {
		this.retwitted = retwitted;
	}
	
	public String getUser() {
		return user;
	}
	
	public void setUser(String user) {
		this.user = user;
	}

}
