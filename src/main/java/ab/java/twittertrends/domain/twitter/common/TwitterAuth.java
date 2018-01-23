package ab.java.twittertrends.domain.twitter.common;

import lombok.Builder;
import lombok.ToString;
import lombok.Value;

@Builder
@Value
@ToString
public class TwitterAuth {

	private String token;
	private String secret;
	private String customerKey;
	private String customerSecret;
}
