package com.github.adrian83.trends.domain.hashtag.web;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.github.adrian83.trends.common.web.SseController;
import com.github.adrian83.trends.domain.hashtag.logic.HashtagService;
import com.github.adrian83.trends.domain.hashtag.model.Hashtag;

import reactor.core.publisher.Flux;

import static com.github.adrian83.trends.common.web.ViewController.HASHTAGS;
import static com.github.adrian83.trends.common.web.ViewController.SSE_CONTENT_TYPE;
import static com.github.adrian83.trends.common.web.ViewController.SSE_PATH;

@RestController
public class HashtagController extends SseController<Hashtag> {

	@Autowired
	private HashtagService hashtagService;

	@GetMapping(value = SSE_PATH + HASHTAGS, produces = SSE_CONTENT_TYPE)
	public Flux<ServerSentEvent<List<Hashtag>>> sseHashtags() {
		return toSse(hashtagService.top());
	}

}
