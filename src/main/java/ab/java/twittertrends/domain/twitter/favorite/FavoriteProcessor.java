package ab.java.twittertrends.domain.twitter.favorite;

import java.util.List;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ab.java.twittertrends.domain.twitter.TwittsSource;
import ab.java.twittertrends.domain.twitter.favorite.repository.FavoriteRepository;
import ab.java.twittertrends.domain.twitter.hashtag.Hashtag;
import ab.java.twittertrends.domain.twitter.hashtag.ImmutableHashtag;

@Component
public class FavoriteProcessor {

	private static final Logger LOGGER = Logger.getLogger(FavoriteProcessor.class.getSimpleName());
	
	private static final int DEF_BUFFER_SIZE = 2;

	@Autowired
	private TwittsSource twittsSource;
	
	@Autowired
	private FavoriteRepository favoriteRepository;
	
	
	@PostConstruct
	public void postCreate() {
		LOGGER.log(Level.INFO, "FavoriteProcessor created");
		persistFavorities();
	}
	
	private void persistFavorities() {
		LOGGER.log(Level.INFO, "Starting persisting favorites");
		
		twittsSource.twitts()
		.filter(s -> s.getFavoriteCount() > 0)
		.map(s -> (Favorite) ImmutableFavorite.builder()
				.id(s.getId())
				.favorite(s.getFavoriteCount())
				.build())
        .buffer(DEF_BUFFER_SIZE)
        .subscribe(new Subscriber<List<Favorite>>() {

			@Override
			public void onComplete() {
				LOGGER.log(Level.INFO, "No more favorities to process");
			}

			@Override
			public void onError(Throwable ex) {
				LOGGER.log(Level.WARNING, "Exception while processing favorities. Exception: ", ex);
			}

			@Override
			public void onNext(List<Favorite> favorities) {
	        	LOGGER.log(Level.INFO, "Persisting {0} favorities", favorities);
	        	favoriteRepository.save(favorities);
			}

			@Override
			public void onSubscribe(Subscription subscription) {
				LOGGER.log(Level.INFO, "New Subscription. ", subscription);
			}
        	
        });
        
	}

}
