package ab.java.twittertrends.domain.twitter.favorite;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "favorites")
public class FavoriteDoc {

	@Id
	private String id;
	private Long twittId;
	private Integer faworite;
	
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
	public Integer getFaworite() {
		return faworite;
	}
	public void setFaworite(Integer faworite) {
		this.faworite = faworite;
	}

}
