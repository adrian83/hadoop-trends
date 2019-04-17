package com.github.adrian83.trends.domain.favorite;

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
public class FavoriteService implements Service<Favorite> {

	private static final Logger LOGGER = LoggerFactory.getLogger(FavoriteService.class);

	@Autowired
	private FavoriteRepository favoriteRepository;
	
	@Autowired
	private FavoriteMapper favoriteMapper;
	
	private ConnectableFlux<List<Favorite>> favorited;
	
	
	@PostConstruct
	public void postCreate() {
		LOGGER.info("Created");
		
		favorited = Flux.interval(Duration.ofSeconds(10))
				.flatMap(i -> favoriteRepository.top(10))
				.map(list -> list.stream().map(favoriteMapper::docToDto).collect(Collectors.toList()))
 				.publish();
		favorited.connect();
		
 		LOGGER.info("Hot observable started");
	}
	
	public Flux<List<Favorite>> favorited() {
		return favorited;
	}
	
	@Override
	@Scheduled(fixedRate = CLEANING_FIXED_RATE_MS, initialDelay = CLEANING_INITIAL_DELAY_MS)
	public void removeUnused() {
		Mono<DeleteResult> result = favoriteRepository.deleteOlderThan(1, TimeUnit.MINUTES);
		result.subscribe(
        		dr -> LOGGER.warn("Twitts removed {}", dr.getDeletedCount()), 
        		t -> LOGGER.error("Exception during removing twitts {}", t));
	}
	
}
