package com.github.adrian83.trends.domain.retwitt;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

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
public class RetwittService implements Service<Retwitt> {

	private static final Logger LOGGER = LoggerFactory.getLogger(RetwittService.class);

	@Autowired
	private RetwittRepository retwittRepository;
	
	private ConnectableFlux<List<Retwitt>> retwitted;
	
	
	@PostConstruct
	public void postCreate() {
		LOGGER.info("Created");
		
		retwitted = Flux.interval(Duration.ofSeconds(10))
				.flatMap(i -> retwittRepository.mostRetwitted(10))
				.map(list -> list.stream().map(this::toRetwitt).collect(Collectors.toList()))
 				.publish();
		retwitted.connect();
		
 		LOGGER.info("Hot observable started");
	}
	
	private Retwitt toRetwitt(RetwittDoc doc) {
		return new Retwitt(doc.getTwittId().toString(), doc.getUsername(), doc.getCount());
	}

	
	public Flux<List<Retwitt>> retwitted() {
		return retwitted;
	}
	
	@Override
	@Scheduled(fixedRate = CLEANING_FIXED_RATE_MS, initialDelay = CLEANING_INITIAL_DELAY_MS)
	public void removeUnused() {
		Mono<DeleteResult> result = retwittRepository.deleteOlderThan(1, TimeUnit.MINUTES);
		result.subscribe(
        		dr -> LOGGER.warn("Twitts removed {}", dr.getDeletedCount()), 
        		t -> LOGGER.error("Exception during removing twitts {}", t));
	}
	
}
