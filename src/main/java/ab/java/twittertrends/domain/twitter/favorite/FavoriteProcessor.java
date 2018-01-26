package ab.java.twittertrends.domain.twitter.favorite;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ab.java.twittertrends.common.Time;
import ab.java.twittertrends.domain.twitter.TwittsSource;
import ab.java.twittertrends.domain.twitter.common.Repository;
import twitter4j.Status;

@Component
public class FavoriteProcessor {

	private static final Logger LOGGER = LoggerFactory.getLogger(FavoriteProcessor.class);
	
	@Autowired
	private TwittsSource twittsSource;
	
	@Autowired
	private Repository<Favorite> favoriteRepository;
	
	
	@PostConstruct
	public void postCreate() {
		LOGGER.info("FavoriteProcessor created");
		persistFavorities();
	}
	
	private boolean shouldProcess(Status status) {
		Status retweetedStatus = status.getRetweetedStatus();
		return retweetedStatus != null 
				&& retweetedStatus.getFavoriteCount() > 0
				&& retweetedStatus.getId() > 0
				&& retweetedStatus.getUser() != null
				&& retweetedStatus.getUser().getScreenName() != null;
	}
	
	private void persistFavorities() {
		LOGGER.info("Starting persisting favorites");
	
		twittsSource.twittsFlux()
		.filter(this::shouldProcess)
		.map(s -> (Favorite) Favorite.builder()
				.twittId(String.valueOf(s.getRetweetedStatus().getId()))
				.count(s.getRetweetedStatus().getFavoriteCount())
				.userName(s.getRetweetedStatus().getUser().getScreenName())
				.updated(Time.utcNow())
				.build())
		.map(favoriteRepository::save)
		.subscribe(mur -> mur.subscribe(ur -> LOGGER.info("Saved favorite: {}", ur.getUpsertedId())));
   
	}

}
