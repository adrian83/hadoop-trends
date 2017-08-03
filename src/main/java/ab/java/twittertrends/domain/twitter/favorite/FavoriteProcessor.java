package ab.java.twittertrends.domain.twitter.favorite;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ab.java.twittertrends.domain.twitter.TwittsSource;

@Component
public class FavoriteProcessor {

	private static final Logger LOGGER = LoggerFactory.getLogger(FavoriteProcessor.class);

	@Autowired
	private TwittsSource twittsSource;
	
	@Autowired
	private FavoriteRepository favoriteRepository;
	
	
	@PostConstruct
	public void postCreate() {
		LOGGER.debug("Starting processing favorites");
		
		persistTwitts();
	}
	
	private void persistTwitts() {
		twittsSource.twitts()
		.filter(s -> s.getFavoriteCount() > 0)
		.map(s -> (Favorite)ImmutableFavorite.builder()
				.id(s.getId())
				.favorite(s.getFavoriteCount())
				.build())
        .buffer(2)
        .subscribe(favoriteRepository::save);
	}
	
	
	
}
