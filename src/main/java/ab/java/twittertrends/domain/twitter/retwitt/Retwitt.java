package ab.java.twittertrends.domain.twitter.retwitt;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Field;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import lombok.Builder;
import lombok.Value;


@Value
@Builder
@JsonSerialize
@JsonDeserialize
public class Retwitt {	
	
	public final static String RETWITTS = "retwitts";
	public static final String ID_LABEL = "id";
	public static final String USER_LABEL = "user_name";
	public static final String TWITT_ID_LABEL = "twitt_id";
	public static final String COUNT_LABEL = "count";
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
