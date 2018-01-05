package ab.java.twittertrends.domain.twitter.reply;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.mongodb.client.result.DeleteResult;

import ab.java.twittertrends.domain.twitter.common.Fetcher;
import ab.java.twittertrends.domain.twitter.common.Repository;
import reactor.core.publisher.ConnectableFlux;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
public class ReplyFetcher implements Fetcher<Reply> {

	private static final Logger LOGGER = Logger.getLogger(ReplyFetcher.class.getSimpleName());

	@Autowired
	private Repository<Reply> replyRepository;
	
	private ConnectableFlux<List<Reply>> replies;
		
	@PostConstruct
	public void postCreate() {
		LOGGER.log(Level.INFO, "Created");
		
		replies = Flux.interval(Duration.ofSeconds(10))
				.flatMap(i -> replyRepository.take(10))	
 				.publish();
 		
		replies.connect();
		LOGGER.log(Level.INFO, "Hot observable started");
	}

	@Override
	public Flux<List<Reply>> elements() {
		return replies;
	}
	
	@Override
	@Scheduled(fixedRate = 60000, initialDelay = 1000*60*60)
	public void removeUnused() {
		Mono<DeleteResult> result = replyRepository.deleteOlderThan(LocalDateTime.now().minusHours(1));
		result.subscribe(
        		dr -> LOGGER.log(Level.INFO, "Replies removed {0}", dr.getDeletedCount()), 
        		t -> LOGGER.log(Level.INFO, "Exception during removing replies {0}", t));
	}
	
}
