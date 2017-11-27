package ab.java.twittertrends.domain.twitter.reply;

import org.immutables.value.Value;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;


@Value.Immutable
@JsonSerialize(as = ImmutableReply.class)
@JsonDeserialize(as = ImmutableReply.class)
public interface Reply {

	String id();
	
	Integer count();
	
	String user();
	
}
