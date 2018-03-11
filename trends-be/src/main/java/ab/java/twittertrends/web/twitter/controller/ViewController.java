package ab.java.twittertrends.web.twitter.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class ViewController {

	private static final String INDEX = "index";
	protected static final String HASHTAGS = "hashtags";
	protected static final String RETWEETS = "retwitts";
	protected static final String FAVORITES = "favorites";
	protected static final String REPLIES = "replies";
	
	protected static final String SSE_CONTENT_TYPE = "text/event-stream";
	
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

	@RequestMapping(value = VIEW_PATH + FAVORITES)
	public String favorites() {
		return FAVORITES;
	}

	@RequestMapping(value = VIEW_PATH + REPLIES)
	public String replies() {
		return REPLIES;
	}

}
