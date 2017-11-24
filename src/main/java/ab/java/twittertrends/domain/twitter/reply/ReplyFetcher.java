package ab.java.twittertrends.domain.twitter.reply;

import java.time.Duration;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ab.java.twittertrends.domain.twitter.common.Repository;
import reactor.core.publisher.ConnectableFlux;
import reactor.core.publisher.Flux;

@Component
public class ReplyFetcher {

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
	
	public Flux<List<Reply>> replies() {
		return replies;
	}
	
}
