package ab.java.twittertrends.web.hashtag;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ab.java.twittertrends.domain.twitter.hashtag.Hashtag;
import ab.java.twittertrends.domain.twitter.hashtag.HashtagFetcher;
import rx.Observable;

@RestController
public class HashtagController {
	

@Autowired
private HashtagFetcher hashtagFetcher;

	@RequestMapping(value = "/hashtags/popular")
	public Observable<List<Hashtag>> starters() {
		
		return hashtagFetcher.hashtags().first();
	}
	
}


