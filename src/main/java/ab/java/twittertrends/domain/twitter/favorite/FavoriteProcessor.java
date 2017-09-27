package ab.java.twittertrends.domain.twitter.favorite;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ab.java.twittertrends.domain.twitter.TwittsSource;
import ab.java.twittertrends.domain.twitter.favorite.repository.FavoriteRepository;

@Component
public class FavoriteProcessor {

	private static final Logger LOGGER = Logger.getLogger(FavoriteProcessor.class.getSimpleName());
	
	private static final int DEF_BUFFER_SIZE = 20;

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
	
		twittsSource.twittsFlux()
		.filter(s -> s.getFavoriteCount() > 0)
		.map(s -> (Favorite) ImmutableFavorite.builder()
				.id(s.getId())
				.favorite(s.getFavoriteCount())
				.build())
        .buffer(DEF_BUFFER_SIZE)
        .subscribe(favoriteRepository::save);
        
	}

}
