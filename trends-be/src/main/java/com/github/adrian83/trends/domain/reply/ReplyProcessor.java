package com.github.adrian83.trends.domain.reply;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.github.adrian83.trends.common.Time;
import com.github.adrian83.trends.domain.status.StatusSource;

import twitter4j.Status;

@Component
public class ReplyProcessor {

	private static final Logger LOGGER = LoggerFactory.getLogger(ReplyProcessor.class);

	@Autowired
	private StatusSource twittsSource;
	@Autowired
	private ReplyRepository replyRepository;

	@PostConstruct
	public void postCreate() {
		LOGGER.info("TwittProcessor created");
		persistTwitts();
	}

	private void persistTwitts() {
		LOGGER.info("Starting persisting twitts");
		twittsSource.twittsFlux().filter(this::completeReply).map(this::toReply).map(replyRepository::save)
				.subscribe(mur -> mur.subscribe(ur -> LOGGER.info("Saved reply: {}", ur.getUpsertedId())),
						t -> LOGGER.error("Exception during processing hashtags {}", t));
	}

	private boolean completeReply(Status status) {
		return status.getInReplyToStatusId() > 0 
		&& status.getInReplyToScreenName() != null;
	}

	private ReplyDoc toReply(Status status) {
		return new ReplyDoc(null, status.getInReplyToStatusId(), status.getInReplyToScreenName(),
				1l, Time.utcNow());


	}

}
