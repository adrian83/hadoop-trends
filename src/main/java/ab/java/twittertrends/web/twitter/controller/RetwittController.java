package ab.java.twittertrends.web.twitter.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import ab.java.twittertrends.domain.twitter.common.Fetcher;
import ab.java.twittertrends.domain.twitter.retwitt.Retwitt;
import reactor.core.publisher.Flux;

@RestController
public class RetwittController extends SseController<Retwitt> {

	@Autowired 
	private Fetcher<Retwitt> retwittFetcher;
	
	@GetMapping(value = "/sse/retwitts", produces = "text/event-stream")
	public Flux<ServerSentEvent<List<Retwitt>>> sseRetwitts() {
		return toSse(retwittFetcher.elements());
	}
	
}
