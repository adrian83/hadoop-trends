package com.github.adrian83.trends.domain.reply;

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
public class ReplyService implements Service<Reply> {

	private static final Logger LOGGER = LoggerFactory.getLogger(ReplyService.class);

	@Autowired
	private ReplyRepository replyRepository;
	
	private ConnectableFlux<List<Reply>> replies;
	
	
	@PostConstruct
	public void postCreate() {
		LOGGER.info("Created");
		
		replies = Flux.interval(Duration.ofSeconds(10))
				.flatMap(i -> replyRepository.top(10))
				.map(list -> list.stream().map(this::toReply).collect(Collectors.toList()))
 				.publish();
		replies.connect();
		
 		LOGGER.info("Hot observable started");
	}
	
	private Reply toReply(ReplyDoc doc) {
		return new Reply(doc.getTwittId().toString(), doc.getUsername(), doc.getCount());
	}

	
	@Override
	public Flux<List<Reply>> top() {
		return replies;
	}
	
	@Override
	@Scheduled(fixedRate = CLEANING_FIXED_RATE_MS, initialDelay = CLEANING_INITIAL_DELAY_MS)
	public void removeUnused() {
		Mono<DeleteResult> result = replyRepository.deleteOlderThan(1, TimeUnit.MINUTES);
		result.subscribe(
        		dr -> LOGGER.warn("Twitts removed {}", dr.getDeletedCount()), 
        		t -> LOGGER.error("Exception during removing twitts {}", t));
	}
	
}
