package com.github.adrian83.trends.common.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class ViewController {

	public static final String INDEX = "index";
	public static final String HASHTAGS = "hashtags";
	public static final String RETWEETS = "retwitts";
	public static final String REPLIES = "replies";
	
	public static final String SSE_CONTENT_TYPE = "text/event-stream";
	
	public static final String VIEW_PATH = "/view/";
	public static final String SSE_PATH = "/sse/";

	@RequestMapping(value = { "/", "/" + INDEX })
	public String index() {
		return INDEX;
	}

	@RequestMapping(value = VIEW_PATH + HASHTAGS)
	public String hashtags() {
		return HASHTAGS;
	}

	@RequestMapping(value = VIEW_PATH + RETWEETS)
	public String retwitts() {
		return RETWEETS;
	}



}
