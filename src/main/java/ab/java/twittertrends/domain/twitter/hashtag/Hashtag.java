package ab.java.twittertrends.domain.twitter.hashtag;

import org.immutables.value.Value;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;


@Value.Immutable
@JsonSerialize(as = ImmutableHashtag.class)
@JsonDeserialize(as = ImmutableHashtag.class)
public interface Hashtag {

	String name();
	
	Long count();
	
}
