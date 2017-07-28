package ab.java.twittertrends.domain.twitter.retwitt;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import rx.Observable;
import rx.observables.ConnectableObservable;

@Component
public class RetwittFetcher {

	private static final Logger LOGGER = LoggerFactory.getLogger(RetwittFetcher.class);

	@Autowired
	private RetwittRepository retwittRepository;
	
	private ConnectableObservable<List<Retwitt>> retwitts;
		
	@PostConstruct
	public void postCreate() {
		LOGGER.debug("Starting reading retwitts");
		
		retwitts = Observable.interval(5, TimeUnit.SECONDS)
				.flatMap(i -> retwittRepository.popularTwitts(10)		
						.map(hdl -> hdl.stream()
								.map(hd -> (Retwitt)ImmutableRetwitt.builder()
										.id(hd.getTwittId())
										.retwitted(hd.getRetwitted())
										.build())
								.collect(Collectors.toList())))
 				.publish();
 		
		retwitts.connect();
	}
	
	public Observable<List<Retwitt>> retwitts() {
		return retwitts;
	}
	
}
