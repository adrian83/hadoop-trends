package ab.java.twittertrends.domain.twitter.favorite;



import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import lombok.Builder;
import lombok.Value;


@JsonSerialize
@JsonDeserialize
@Builder
@Value
@Document(collection = Favorite.FAVORITES)
public class Favorite {
	
	public static final String FAVORITES = "favorites";
	public static final String ID_LABEL = "id";
	public static final String USER_LABEL = "user_name";
	public static final String COUNT_LABEL = "count";
	public static final String TWITT_ID_LABEL = "twitt_id";
	public static final String LAST_UPDATE_LABEL = "updated";
	
	@Id
	@Field(ID_LABEL)
	private String documentId;
	@Field(TWITT_ID_LABEL)
	private String twittId;
	@Field(COUNT_LABEL)
	private int count;
	@Field(USER_LABEL)
	private String userName;
	@Field(LAST_UPDATE_LABEL)
	private Long updated;
}
