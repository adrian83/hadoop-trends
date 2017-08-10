package ab.java.twittertrends.domain.twitter.hashtag;

import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ab.java.twittertrends.domain.twitter.hashtag.repository.HashtagRepository;
import rx.Observable;
import rx.observables.ConnectableObservable;

@Component
public class HashtagFetcher {

	private static final Logger LOGGER = LoggerFactory.getLogger(HashtagFetcher.class);

	@Autowired
	private HashtagRepository mongoRepository;
	
	private ConnectableObservable<List<Hashtag>> hashtags;
	
	
	@PostConstruct
	public void postCreate() {
		LOGGER.debug("Starting reading hashtags");
		
 		hashtags = Observable.interval(10, TimeUnit.SECONDS)
				.flatMap(i -> mongoRepository.popularHashtags(10))
 				.publish();
 		
 		hashtags.connect();
	}
	
	public Observable<List<Hashtag>> hashtags() {
		return hashtags;
	}
	
}
