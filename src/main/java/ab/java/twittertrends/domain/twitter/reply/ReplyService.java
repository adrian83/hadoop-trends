package ab.java.twittertrends.domain.twitter.reply;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.mongodb.client.result.DeleteResult;

import ab.java.twittertrends.domain.twitter.common.Service;
import ab.java.twittertrends.domain.twitter.common.Repository;
import reactor.core.publisher.ConnectableFlux;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
public class ReplyService implements Service<Reply> {

	private static final Logger LOGGER = LoggerFactory.getLogger(ReplyService.class);
	
	@Autowired
	private Repository<Reply> replyRepository;

	private ConnectableFlux<List<Reply>> replies;

	@PostConstruct
	public void postCreate() {
		LOGGER.info("Created");

		replies = Flux.interval(Duration.ofSeconds(10)).flatMap(i -> replyRepository.take(10)).publish();

		replies.connect();
		LOGGER.info("Hot observable started");
	}

	@Override
	public Flux<List<Reply>> elements() {
		return replies;
	}

	@Override
	@Scheduled(fixedRate = CLEANING_FIXED_RATE_MS, initialDelay = CLEANING_INITIAL_DELAY_MS)
	public void removeUnused() {
		Mono<DeleteResult> result = replyRepository.deleteOlderThan(1, TimeUnit.MINUTES);
		result.subscribe(
				dr -> LOGGER.warn("Replies removed {}", dr.getDeletedCount()),
				t -> LOGGER.error("Exception during removing replies {}", t));
	}

}
