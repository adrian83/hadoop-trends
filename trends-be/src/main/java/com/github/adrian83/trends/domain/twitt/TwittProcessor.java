package com.github.adrian83.trends.domain.twitt;


import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.github.adrian83.trends.common.Time;
import com.github.adrian83.trends.domain.status.StatusSource;

import twitter4j.Status;

@Component
public class TwittProcessor {

	private static final Logger LOGGER = LoggerFactory.getLogger(TwittProcessor.class);

	@Autowired
	private StatusSource twittsSource;
	@Autowired
	private TwittRepository twittRepository;

	@PostConstruct
	public void postCreate() {
		LOGGER.info("TwittProcessor created");
		persistTwitts();
	}

	private void persistTwitts() {
		LOGGER.info("Starting persisting twitts");
		twittsSource.twittsFlux()
			.map(this::toTwitt)
			.map(twittRepository::save)
			.subscribe(mur -> mur.subscribe(
					ur -> LOGGER.info("Saved hashtag: {}", ur.getUpsertedId())), 
					t -> LOGGER.error("Exception during processing hashtags {}", t));
	}

	private TwittDoc toTwitt(Status status) {
		return new TwittDoc(null, status.getId(), status.getUser().getScreenName(), status.getFavoriteCount(), status.getRetweetCount(), Time.utcNow());
	}
	
}
