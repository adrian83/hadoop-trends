package com.github.adrian83.trends.domain.twitt;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.github.adrian83.trends.common.web.SseController;

import reactor.core.publisher.Flux;

import static com.github.adrian83.trends.common.web.ViewController.RETWEETS;
import static com.github.adrian83.trends.common.web.ViewController.FAVORITES;
import static com.github.adrian83.trends.common.web.ViewController.SSE_CONTENT_TYPE;
import static com.github.adrian83.trends.common.web.ViewController.SSE_PATH;

@RestController
public class TwittController extends SseController<TwittDoc> {

	@Autowired
	private TwittService twittService;

	
	@GetMapping(value = SSE_PATH + RETWEETS, produces = SSE_CONTENT_TYPE)
	public Flux<ServerSentEvent<List<TwittDoc>>> sseRetwitted() {
		return toSse(twittService.retwitted());
	}

	@GetMapping(value = SSE_PATH + FAVORITES, produces = SSE_CONTENT_TYPE)
	public Flux<ServerSentEvent<List<TwittDoc>>> sseFavorites() {
		return toSse(twittService.favorites());
	}
	
}
