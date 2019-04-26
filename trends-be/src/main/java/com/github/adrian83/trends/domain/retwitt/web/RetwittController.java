package com.github.adrian83.trends.domain.retwitt.web;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.github.adrian83.trends.domain.retwitt.logic.RetwittService;
import com.github.adrian83.trends.domain.retwitt.model.Retwitt;
import com.github.adrian83.trends.web.BaseController;

import reactor.core.publisher.Flux;

@RestController
public class RetwittController extends BaseController<Retwitt> {

	public static final String RETWEETS = "retwitts";
	
	@Autowired
	private RetwittService retwittService;

	
	@GetMapping(value = SSE_PATH + RETWEETS, produces = SSE_CONTENT_TYPE)
	public Flux<ServerSentEvent<List<Retwitt>>> sseRetwitted() {
		return toSse(retwittService.top());
	}

}
