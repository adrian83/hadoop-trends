package com.github.adrian83.trends.domain.twitt;

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
public class TwittService implements Service<TwittDoc> {

	private static final Logger LOGGER = LoggerFactory.getLogger(TwittService.class);

	@Autowired
	private TwittRepository twittRepository;
	
	private ConnectableFlux<List<TwittDoc>> favoriteTwitts;
	private ConnectableFlux<List<TwittDoc>> retwittedTwitts;
	private ConnectableFlux<List<TwittDoc>> repliedTwitts;
	
	
	@PostConstruct
	public void postCreate() {
		LOGGER.info("Created");
		
		favoriteTwitts = Flux.interval(Duration.ofSeconds(10))
				.flatMap(i -> twittRepository.mostFavorite(10))
 				.publish();
		favoriteTwitts.connect();
		
		retwittedTwitts = Flux.interval(Duration.ofSeconds(10))
				.flatMap(i -> twittRepository.mostRetwitted(10))
 				.publish();
		retwittedTwitts.connect();
		
 		LOGGER.info("Hot observable started");
	}
	

	public Flux<List<TwittDoc>> favorites() {
		return favoriteTwitts;
	}
	
	public Flux<List<TwittDoc>> retwitted() {
		return retwittedTwitts;
	}
	
	@Override
	@Scheduled(fixedRate = CLEANING_FIXED_RATE_MS, initialDelay = CLEANING_INITIAL_DELAY_MS)
	public void removeUnused() {
		Mono<DeleteResult> result = twittRepository.deleteOlderThan(1, TimeUnit.MINUTES);
		result.subscribe(
        		dr -> LOGGER.warn("Twitts removed {}", dr.getDeletedCount()), 
        		t -> LOGGER.error("Exception during removing twitts {}", t));
	}
	
}
