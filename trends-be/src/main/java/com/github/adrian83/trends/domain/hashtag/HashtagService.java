package com.github.adrian83.trends.domain.hashtag;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.github.adrian83.trends.common.Service;
import com.mongodb.client.result.DeleteResult;

import reactor.core.publisher.ConnectableFlux;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
public class HashtagService implements Service<HashtagDoc> {

	private static final Logger LOGGER = LoggerFactory.getLogger(HashtagService.class);

	@Autowired
	private HashtagRepository hashtagRepository;
	
	private ConnectableFlux<List<HashtagDoc>> hashtags;
	
	
	@PostConstruct
	public void postCreate() {
		LOGGER.info("Created");
		
 		hashtags = Flux.interval(Duration.ofSeconds(10))
				.flatMap(i -> hashtagRepository.top(10))
 				.publish();
 		
 		hashtags.connect();
 		LOGGER.info("Hot observable started");
	}
	
	public Flux<List<HashtagDoc>> elements() {
		return hashtags;
	}
	
	@Override
	@Scheduled(fixedRate = CLEANING_FIXED_RATE_MS, initialDelay = CLEANING_INITIAL_DELAY_MS)
	public void removeUnused() {
		Mono<DeleteResult> result = hashtagRepository.deleteOlderThan(1, TimeUnit.MINUTES);
		result.subscribe(
        		dr -> LOGGER.warn("Hashtags removed {}", dr.getDeletedCount()), 
        		t -> LOGGER.error("Exception during removing hashtags {}", t));
	}
	
}
