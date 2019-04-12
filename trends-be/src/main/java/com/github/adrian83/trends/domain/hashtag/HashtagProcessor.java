package com.github.adrian83.trends.domain.hashtag;

import java.util.function.Function;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.github.adrian83.trends.domain.status.StatusSource;

import twitter4j.Status;

@Component
public class HashtagProcessor {

	private static final Logger LOGGER = LoggerFactory.getLogger(HashtagProcessor.class);

	private static final int DEF_BUFFER_SIZE = 100;

	@Autowired
	private StatusSource twittsSource;
	@Autowired
	private HashtagFinder hashtagFinder;
	@Autowired
	private HashtagRepository hashtagRepository;

	@PostConstruct
	public void postCreate() {
		LOGGER.info("HashtagProcessor created");
		persistHashtags();
	}

	private void persistHashtags() {
		LOGGER.info("Starting persisting hashtags");
		twittsSource.twittsFlux().map(Status::getText).flatMap(hashtagFinder::findHashtags).buffer(DEF_BUFFER_SIZE)
				.flatMapIterable(hashtags -> hashtags.stream()
						.collect(Collectors.groupingBy(Function.identity(), Collectors.counting())).entrySet().stream()
						.map(e -> new HashtagDoc(null, e.getKey(), e.getValue().intValue(), null))
						.collect(Collectors.toList()))
				.map(hashtagRepository::save)
				.subscribe(mur -> mur.subscribe(ur -> LOGGER.info("Saved hashtag: {}", ur.getUpsertedId())),
						t -> LOGGER.error("Exception during processing hashtags {}", t));
	}

}
