package com.github.adrian83.trends.domain.hashtag;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.github.adrian83.trends.common.Service;
import com.github.adrian83.trends.common.web.SseController;

import reactor.core.publisher.Flux;

import static com.github.adrian83.trends.common.web.ViewController.HASHTAGS;
import static com.github.adrian83.trends.common.web.ViewController.SSE_CONTENT_TYPE;
import static com.github.adrian83.trends.common.web.ViewController.SSE_PATH;

@RestController
public class HashtagController extends SseController<HashtagDoc> {

	@Autowired
	private Service<HashtagDoc> hashtagFetcher;

	@GetMapping(value = SSE_PATH + HASHTAGS, produces = SSE_CONTENT_TYPE)
	public Flux<ServerSentEvent<List<HashtagDoc>>> sseHashtags() {
		return toSse(hashtagFetcher.elements());
	}

}
