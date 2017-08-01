package ab.java.twittertrends.web.twitter.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ab.java.twittertrends.domain.twitter.retwitt.Retwitt;
import ab.java.twittertrends.domain.twitter.retwitt.RetwittFetcher;
import rx.Observable;

@RestController
public class RetwittController {

	@Autowired 
	private RetwittFetcher retwittFetcher;
	
	@RequestMapping(value = "/retwitts")
	public Observable<List<Retwitt>> retwitts() {
		return retwittFetcher.retwitts().first();
	}
	
}
