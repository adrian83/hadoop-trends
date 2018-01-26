package ab.java.twittertrends.web.twitter.controller;

import java.util.List;

import org.springframework.http.codec.ServerSentEvent;

import reactor.core.publisher.Flux;

public class SseController<T> extends ViewController {

	protected Flux<ServerSentEvent<List<T>>> toSse(Flux<List<T>> elements){
		return elements.map(l -> ServerSentEvent.builder(l).build());
	}
	
}
