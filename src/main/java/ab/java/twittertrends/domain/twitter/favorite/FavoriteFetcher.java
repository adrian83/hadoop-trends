package ab.java.twittertrends.domain.twitter.favorite;

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
public class FavoriteFetcher implements Fetcher<Favorite> {

	private static final Logger LOGGER = Logger.getLogger(FavoriteProcessor.class.getSimpleName());

	@Autowired
	private Repository<Favorite> favoriteRepository;
	
	private ConnectableFlux<List<Favorite>> favorites;
		
	@PostConstruct
	public void postCreate() {
		LOGGER.log(Level.INFO, "Created");
		
		favorites = Flux.interval(Duration.ofSeconds(10))
				.flatMap(i -> favoriteRepository.take(10))
 				.publish();
 		
		favorites.connect();
		LOGGER.log(Level.INFO, "Hot Flux started");
	}

	@Override
	public Flux<List<Favorite>> elements() {
		return favorites;
	}
	
	@Override
	@Scheduled(fixedRate = 60000, initialDelay = 1000*60*60)
	public void removeUnused() {
		Mono<DeleteResult> result = favoriteRepository.deleteOlderThan(LocalDateTime.now().minusHours(1));
		result.subscribe(
        		dr -> LOGGER.log(Level.INFO, "Favorites removed {0}", dr.getDeletedCount()), 
        		t -> LOGGER.log(Level.INFO, "Exception during removing favorites {0}", t));
	}
	
}
