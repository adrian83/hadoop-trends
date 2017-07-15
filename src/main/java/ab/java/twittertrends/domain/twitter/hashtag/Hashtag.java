package ab.java.twittertrends.domain.twitter.hashtag;

import org.immutables.value.Value;

@Value.Immutable
public interface Hashtag {

	String name();
	
	Long count();
	
}
