package ab.java.twittertrends.domain.twitter.retwitt;

import org.immutables.value.Value;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;


@Value.Immutable
@JsonSerialize(as = ImmutableRetwitt.class)
@JsonDeserialize(as = ImmutableRetwitt.class)
public interface Retwitt {

	long id();
	
	int retwitted();
	
}
