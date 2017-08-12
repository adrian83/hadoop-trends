package ab.java.twittertrends.domain.twitter.retwitt;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ab.java.twittertrends.domain.twitter.retwitt.repository.RetwittRepository;
import rx.Observable;
import rx.observables.ConnectableObservable;

@Component
public class RetwittFetcher {

	private static final Logger LOGGER = Logger.getLogger(RetwittFetcher.class.getSimpleName());

	@Autowired
	private RetwittRepository retwittRepository;
	
	private ConnectableObservable<List<Retwitt>> retwitts;
		
	@PostConstruct
	public void postCreate() {
		LOGGER.log(Level.INFO, "Created");
		
		retwitts = Observable.interval(5, TimeUnit.SECONDS)
				.flatMap(i -> retwittRepository.mostRetwitted(10))
 				.publish();
 		
		retwitts.connect();
		LOGGER.log(Level.INFO, "Hot observable started");
	}
	
	public Observable<List<Retwitt>> retwitts() {
		return retwitts;
	}
	
}
