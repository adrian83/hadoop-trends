package ab.java.twittertrends.domain.twitter.reply;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ab.java.twittertrends.domain.twitter.TwittsSource;
import ab.java.twittertrends.domain.twitter.reply.repository.ReplyRepository;

import reactor.core.publisher.Mono;
import twitter4j.Status;

@Component
public class ReplyProcessor {

	private static final Logger LOGGER = Logger.getLogger(ReplyProcessor.class.getSimpleName());
	
	@Autowired
	private TwittsSource twittsSource;
	
	@Autowired
	private ReplyRepository replyRepository;
	
	
	@PostConstruct
	public void postCreate() {
		LOGGER.log(Level.INFO, "ReplyProcessor created");
		persistReplies();
	}
	
	private boolean shouldProcess(Status status) {
		return status.getInReplyToStatusId() > 0 
				&& status.getInReplyToScreenName() != null;
	}
	
	private void persistReplies() {
		LOGGER.log(Level.INFO, "Starting persisting replies");
		
		twittsSource.twittsFlux()
		.filter(this::shouldProcess)
		.map(s -> (Reply)ImmutableReply.builder()
				.id(String.valueOf(s.getInReplyToStatusId()))
				.count(1)
				.user(s.getInReplyToScreenName())
				.build())
        .map(replyRepository::save)
        .map(Mono::block)
        .subscribe(ur -> LOGGER.log(Level.INFO, "Saved reply: {0}", ur.getUpsertedId()));     
        
	}
	
}
