package ab.java.twittertrends.domain.twitter.favorite;

import org.immutables.value.Value;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@Value.Immutable
@JsonSerialize(as = ImmutableFavorite.class)
@JsonDeserialize(as = ImmutableFavorite.class)
public interface Favorite {
	
	String id();
	
	int favorite();
	
	String user();
}
