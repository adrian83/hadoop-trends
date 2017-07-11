package ab.java.twittertrends.domain.twitter.domain;

import org.immutables.value.Value;

@Value.Immutable
public interface TwitterAuth {

	String token();
	String secret();
	String customerKey();
	String customerSecret();

}
