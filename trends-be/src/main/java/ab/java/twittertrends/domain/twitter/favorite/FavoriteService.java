package ab.java.twittertrends.domain.twitter.favorite;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.mongodb.client.result.DeleteResult;

import ab.java.twittertrends.domain.twitter.common.Service;
import ab.java.twittertrends.domain.twitter.common.Repository;
import reactor.core.publisher.ConnectableFlux;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
public class FavoriteService implements Service<Favorite> {

	private static final Logger LOGGER = LoggerFactory.getLogger(FavoriteService.class);

	@Autowired
	private Repository<Favorite> favoriteRepository;
	
	private ConnectableFlux<List<Favorite>> favorites;
		
	@PostConstruct
	public void postCreate() {
		LOGGER.info("Created");
		
		favorites = Flux.interval(Duration.ofSeconds(10))
				.flatMap(i -> favoriteRepository.take(10))
 				.publish();
 		
		favorites.connect();
		LOGGER.info("Hot Flux started");
	}

	@Override
	public Flux<List<Favorite>> elements() {
		return favorites;
	}
	
	@Override
	@Scheduled(fixedRate = CLEANING_FIXED_RATE_MS, initialDelay = CLEANING_INITIAL_DELAY_MS)
	public void removeUnused() {
		Mono<DeleteResult> result = favoriteRepository.deleteOlderThan(1, TimeUnit.MINUTES);
		result.subscribe(
        		dr -> LOGGER.warn("Favorites removed {}", dr.getDeletedCount()), 
        		t -> LOGGER.error("Exception during removing favorites {}", t));
	}
	
}
