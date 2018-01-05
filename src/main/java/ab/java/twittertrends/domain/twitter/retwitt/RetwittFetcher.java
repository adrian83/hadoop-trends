package ab.java.twittertrends.domain.twitter.retwitt;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.mongodb.client.result.DeleteResult;

import ab.java.twittertrends.domain.twitter.common.Fetcher;
import ab.java.twittertrends.domain.twitter.common.Repository;
import reactor.core.publisher.ConnectableFlux;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
public class RetwittFetcher implements Fetcher<Retwitt> {

	private static final Logger LOGGER = LoggerFactory.getLogger(RetwittFetcher.class);

	@Autowired
	private Repository<Retwitt> retwittRepository;
	
	private ConnectableFlux<List<Retwitt>> retwitts;
		
	@PostConstruct
	public void postCreate() {
		LOGGER.info("Created");
		
		retwitts = Flux.interval(Duration.ofSeconds(10))
				.flatMap(i -> retwittRepository.take(10))
 				.publish();
 		
		retwitts.connect();
		LOGGER.info("Hot observable started");
	}
	
	@Override
	public Flux<List<Retwitt>> elements() {
		return retwitts;
	}
	
	@Override
	@Scheduled(fixedRate = 60000, initialDelay = 1000*60)
	public void removeUnused() {
		Mono<DeleteResult> result = retwittRepository.deleteOlderThan(LocalDateTime.now().minusMinutes(1));
		result.subscribe(
        		dr -> LOGGER.warn("Retwitts removed {0}", dr.getDeletedCount()), 
        		t -> LOGGER.error("Exception during removing retwitts {0}", t));
	}
}
