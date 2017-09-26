package ab.java.twittertrends.web.twitter.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ab.java.twittertrends.domain.twitter.retwitt.Retwitt;
import ab.java.twittertrends.domain.twitter.retwitt.RetwittFetcher;
import reactor.core.publisher.Flux;

@RestController
public class RetwittController {

	@Autowired 
	private RetwittFetcher retwittFetcher;
	
	@RequestMapping(value = "/retwitts")
	public Flux<List<Retwitt>> retwitts() {
		return retwittFetcher.retwitts().take(1);
	}
	
	@GetMapping(value = "/sse/retwitts", produces = "text/event-stream")
	public Flux<ServerSentEvent<List<Retwitt>>> sseRetwitts() {
		return retwittFetcher.retwitts().map(l -> ServerSentEvent.builder(l).build());
	}
	
}
