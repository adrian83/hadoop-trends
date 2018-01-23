package ab.java.twittertrends.domain.twitter.hashtag;



import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Field;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import lombok.Builder;
import lombok.Value;



@JsonSerialize()
@JsonDeserialize()
@Value
@Builder
public class Hashtag {
	
	public static final String HASHTAGS = "hashtags";
	public static final String ID_LABEL = "id";
	public static final String NAME_LABEL = "name";
	public static final String COUNT_LABEL = "count";
	public static final String LAST_UPDATE_LABEL = "updated";

	@Id
	@Field(ID_LABEL)
	private String documentId;
	@Field(NAME_LABEL)
	private String name;
	@Field(COUNT_LABEL)
	private int count;
	@Field(LAST_UPDATE_LABEL)
	private Long updated;
	
}
