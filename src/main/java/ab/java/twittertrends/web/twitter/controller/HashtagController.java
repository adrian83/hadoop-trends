package ab.java.twittertrends.web.twitter.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import ab.java.twittertrends.domain.twitter.common.Service;
import ab.java.twittertrends.domain.twitter.hashtag.Hashtag;
import reactor.core.publisher.Flux;

@RestController
public class HashtagController extends SseController<Hashtag> {

	@Autowired
	private Service<Hashtag> hashtagFetcher;

	@GetMapping(value = SSE_PATH + HASHTAGS, produces = SSE_CONTENT_TYPE)
	public Flux<ServerSentEvent<List<Hashtag>>> sseHashtags() {
		return toSse(hashtagFetcher.elements());
	}

}
