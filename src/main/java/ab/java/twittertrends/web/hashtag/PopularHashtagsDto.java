package ab.java.twittertrends.web.hashtag;

import java.time.LocalDate;
import java.util.List;

import ab.java.twittertrends.domain.twitter.hashtag.Hashtag;

public class PopularHashtagsDto {

	private final LocalDate time;
	private final List<Hashtag> hashtags;

	public PopularHashtagsDto(LocalDate time, List<Hashtag> hashtags) {
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

	
}
