package ab.java.twittertrends.web.twitter.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ab.java.twittertrends.domain.twitter.hashtag.Hashtag;
import ab.java.twittertrends.domain.twitter.hashtag.HashtagFetcher;
import rx.Observable;

@RestController
public class HashtagController {

	@Autowired
	private HashtagFetcher hashtagFetcher;

	@RequestMapping(value = "/hashtags")
	public Observable<List<Hashtag>> hashtags() {
		return hashtagFetcher.hashtags().first();
	}

	@GetMapping(value = "/sse/hashtags", produces = "text/event-stream")
	public Observable<ServerSentEvent<List<Hashtag>>> sseHashtags() {
		return hashtagFetcher.hashtags().map(l -> ServerSentEvent.builder(l).build());
	}

}
