package ab.java.twittertrends.domain.twitter.reply.repository;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = ReplyDoc.REPLIES)
public class ReplyDoc {

	public final static String REPLIES = "replies";
	
	@Id
	private String id;
	private Long twittId;
	private Integer count;
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
	
	public Integer getCount() {
		return count;
	}
	
	public void setCount(Integer count) {
		this.count = count;
	}
	
	public String getUser() {
		return user;
	}
	
	public void setUser(String user) {
		this.user = user;
	}

}
