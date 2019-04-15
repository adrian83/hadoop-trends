package com.github.adrian83.trends.domain.favorite;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.github.adrian83.trends.common.Time;
import com.github.adrian83.trends.domain.status.StatusSource;

import twitter4j.Status;

@Component
public class FavoriteProcessor {

	private static final Logger LOGGER = LoggerFactory.getLogger(FavoriteProcessor.class);

	@Autowired
	private StatusSource twittsSource;
	@Autowired
	private FavoriteRepository favoriteRepository;

	@PostConstruct
	public void postCreate() {
		LOGGER.info("TwittProcessor created");
		persistTwitts();
	}

	private void persistTwitts() {
		LOGGER.info("Starting persisting twitts");
		twittsSource.twittsFlux().filter(this::completeFavorite).map(this::toFavorite).map(favoriteRepository::save)
				.subscribe(mur -> mur.subscribe(ur -> LOGGER.info("Saved hashtag: {}", ur.getUpsertedId())),
						t -> LOGGER.error("Exception during processing hashtags {}", t));
	}
	
	private boolean completeFavorite(Status status) {
		Status retweetedStatus = status.getRetweetedStatus();
		return retweetedStatus != null && 
				retweetedStatus.getFavoriteCount() > 0 && 
				retweetedStatus.getId() > 0 && 
				retweetedStatus.getUser() != null && 
				retweetedStatus.getUser().getScreenName() != null;
	}

	private FavoriteDoc toFavorite(Status status) {
		Status retwittStatus = status.getRetweetedStatus();
		return new FavoriteDoc(retwittStatus.getId(), retwittStatus.getUser().getScreenName(),
				retwittStatus.getFavoriteCount(), Time.utcNow());
	}

}
