package ab.java.twittertrends.domain.twitter.favorite.repository;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import ab.java.twittertrends.domain.twitter.common.TwitterDoc;

@Document(collection = FavoriteDoc.FAVORITES)
class FavoriteDoc extends TwitterDoc {
	
	public final static String FAVORITES = "favorites";

	@Id
	private String id;
	private String twittId;
	private Integer favorite;
	private String user;
	
	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	public String getTwittId() {
		return twittId;
	}
	
	public void setTwittId(String twittId) {
		this.twittId = twittId;
	}

	public Integer getFavorite() {
		return favorite;
	}

	public void setFavorite(Integer favorite) {
		this.favorite = favorite;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

}
