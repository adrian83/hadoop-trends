package ab.java.twittertrends.domain.twitter.reply;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ab.java.twittertrends.domain.twitter.TwittsSource;
import ab.java.twittertrends.domain.twitter.reply.repository.ReplyRepository;

import twitter4j.Status;

@Component
public class ReplyProcessor {

	private static final Logger LOGGER = LoggerFactory.getLogger(ReplyProcessor.class);
	
	@Autowired
	private TwittsSource twittsSource;
	
	@Autowired
	private ReplyRepository replyRepository;
	
	
	@PostConstruct
	public void postCreate() {
		LOGGER.info("ReplyProcessor created");
		persistReplies();
	}
	
	private boolean shouldProcess(Status status) {
		return status.getInReplyToStatusId() > 0 
				&& status.getInReplyToScreenName() != null;
	}
	
	private void persistReplies() {
		LOGGER.info("Starting persisting replies");
		
		twittsSource.twittsFlux()
		.filter(this::shouldProcess)
		.map(s -> (Reply)ImmutableReply.builder()
				.id(String.valueOf(s.getInReplyToStatusId()))
				.count(1)
				.user(s.getInReplyToScreenName())
				.build())
        .map(replyRepository::save)
		.subscribe(mur -> mur.subscribe(ur -> LOGGER.info("Saved reply: {}", ur.getUpsertedId())));   
        
	}
	
}
