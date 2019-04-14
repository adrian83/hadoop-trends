package com.github.adrian83.trends.domain.retwitt;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.github.adrian83.trends.common.Time;
import com.github.adrian83.trends.domain.status.StatusSource;

import twitter4j.Status;

@Component
public class RetwittProcessor {

	private static final Logger LOGGER = LoggerFactory.getLogger(RetwittProcessor.class);

	@Autowired
	private StatusSource twittsSource;
	@Autowired
	private RetwittRepository retwittRepository;

	@PostConstruct
	public void postCreate() {
		LOGGER.info("TwittProcessor created");
		persistTwitts();
	}

	private void persistTwitts() {
		LOGGER.info("Starting persisting twitts");
		twittsSource.twittsFlux().filter(this::completeRetwitt).map(this::toRetwitt).map(retwittRepository::save)
				.subscribe(mur -> mur.subscribe(ur -> LOGGER.info("Saved hashtag: {}", ur.getUpsertedId())),
						t -> LOGGER.error("Exception during processing hashtags {}", t));
	}

	private boolean completeRetwitt(Status status) {
		Status retwittStatus = status.getRetweetedStatus();
		return retwittStatus != null && retwittStatus.getId() > 0 && retwittStatus.getUser() != null
				&& retwittStatus.getUser().getScreenName() != null && retwittStatus.getRetweetCount() > 0;
	}

	private RetwittDoc toRetwitt(Status status) {
		Status retwittStatus = status.getRetweetedStatus();
		return new RetwittDoc(null, retwittStatus.getId(), retwittStatus.getUser().getScreenName(),
				retwittStatus.getRetweetCount(), Time.utcNow());

	}

}
