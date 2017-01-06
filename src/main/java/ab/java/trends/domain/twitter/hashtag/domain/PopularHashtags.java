package ab.java.trends.domain.twitter.hashtag.domain;

import java.time.LocalDate;
import java.util.List;
import java.util.function.Function;

public class PopularHashtags {

	private final LocalDate time;
	private final List<Hashtag> hashtags;

	public PopularHashtags(LocalDate time, List<Hashtag> hashtags) {
		super();
		this.time = time;
		this.hashtags = hashtags;
	}

	public LocalDate getTime() {
		return time;
	}

	public List<Hashtag> getHashtags() {
		return hashtags;
	}
	
	public <T> T map(Function<PopularHashtags, T> fn) {
		return fn.apply(this);
	}

}
