package ab.java.twittertrends.domain.twitter.hashtag;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ab.java.twittertrends.domain.twitter.hashtag.repository.HashtagRepository;
import rx.Observable;
import rx.observables.ConnectableObservable;

@Component
public class HashtagFetcher {

	private static final Logger LOGGER = Logger.getLogger(HashtagFetcher.class.getSimpleName());

	@Autowired
	private HashtagRepository hashtagRepository;
	
	private ConnectableObservable<List<Hashtag>> hashtags;
	
	
	@PostConstruct
	public void postCreate() {
		LOGGER.log(Level.INFO, "Created");
		
 		hashtags = Observable.interval(10, TimeUnit.SECONDS)
				.flatMap(i -> hashtagRepository.popularHashtags(10))
 				.publish();
 		
 		hashtags.connect();
 		LOGGER.log(Level.INFO, "Hot observable started");
	}
	
	public Observable<List<Hashtag>> hashtags() {
		return hashtags;
	}
	
}
