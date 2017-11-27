package ab.java.twittertrends.domain.twitter.retwitt;

import java.time.Duration;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ab.java.twittertrends.domain.twitter.common.Fetcher;
import ab.java.twittertrends.domain.twitter.common.Repository;
import reactor.core.publisher.ConnectableFlux;
import reactor.core.publisher.Flux;

@Component
public class RetwittFetcher implements Fetcher<Retwitt> {

	private static final Logger LOGGER = Logger.getLogger(RetwittFetcher.class.getSimpleName());

	@Autowired
	private Repository<Retwitt> retwittRepository;
	
	private ConnectableFlux<List<Retwitt>> retwitts;
		
	@PostConstruct
	public void postCreate() {
		LOGGER.log(Level.INFO, "Created");
		
		retwitts = Flux.interval(Duration.ofSeconds(10))
				.flatMap(i -> retwittRepository.take(10))
 				.publish();
 		
		retwitts.connect();
		LOGGER.log(Level.INFO, "Hot observable started");
	}
	
	@Override
	public Flux<List<Retwitt>> elements() {
		return retwitts;
	}
	
}
