package ab.java.twittertrends.domain.twitter.retwitt;

import java.time.Duration;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ab.java.twittertrends.domain.twitter.retwitt.repository.RetwittRepository;
import reactor.core.publisher.ConnectableFlux;
import reactor.core.publisher.Flux;

@Component
public class RetwittFetcher {

	private static final Logger LOGGER = Logger.getLogger(RetwittFetcher.class.getSimpleName());

	@Autowired
	private RetwittRepository retwittRepository;
	
	private ConnectableFlux<List<Retwitt>> retwitts;
		
	@PostConstruct
	public void postCreate() {
		LOGGER.log(Level.INFO, "Created");
		
		retwitts = Flux.interval(Duration.ofSeconds(5l))
				.flatMap(i -> retwittRepository.mostRetwitted(10))
 				.publish();
 		
		retwitts.connect();
		LOGGER.log(Level.INFO, "Hot observable started");
	}
	
	public Flux<List<Retwitt>> retwitts() {
		return retwitts;
	}
	
}
