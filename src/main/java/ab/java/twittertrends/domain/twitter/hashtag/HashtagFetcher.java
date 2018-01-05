package ab.java.twittertrends.domain.twitter.hashtag;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;

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
public class HashtagFetcher implements Fetcher<Hashtag> {

	private static final Logger LOGGER = Logger.getLogger(HashtagFetcher.class.getSimpleName());

	@Autowired
	private Repository<Hashtag> hashtagRepository;
	
	private ConnectableFlux<List<Hashtag>> hashtags;
	
	
	@PostConstruct
	public void postCreate() {
		LOGGER.log(Level.INFO, "Created");
		
 		hashtags = Flux.interval(Duration.ofSeconds(10))
				.flatMap(i -> hashtagRepository.take(10))
 				.publish();
 		
 		hashtags.connect();
 		LOGGER.log(Level.INFO, "Hot observable started");
	}
	
	@Override
	public Flux<List<Hashtag>> elements() {
		return hashtags;
	}
	
	@Override
	@Scheduled(fixedRate = 60000, initialDelay = 1000*60*60)
	public void removeUnused() {
		Mono<DeleteResult> result = hashtagRepository.deleteOlderThan(LocalDateTime.now().minusHours(1));
		result.subscribe(
        		dr -> LOGGER.log(Level.INFO, "Hashtags removed {0}", dr.getDeletedCount()), 
        		t -> LOGGER.log(Level.INFO, "Exception during removing hashtags {0}", t));
	}
	
}
