package ab.java.twittertrends.domain.twitter.reply;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ab.java.twittertrends.domain.twitter.TwittsSource;
import ab.java.twittertrends.domain.twitter.reply.repository.ReplyRepository;

@Component
public class ReplyProcessor {

	private static final Logger LOGGER = Logger.getLogger(ReplyProcessor.class.getSimpleName());

	private static final int DEF_BUFFER_SIZE = 1000;
	
	@Autowired
	private TwittsSource twittsSource;
	
	@Autowired
	private ReplyRepository replyRepository;
	
	
	@PostConstruct
	public void postCreate() {
		LOGGER.log(Level.INFO, "ReplyProcessor created");
		persistReplies();
	}
	
	private void persistReplies() {
		LOGGER.log(Level.INFO, "Starting persisting replies");
		
		twittsSource.twitts()
		.filter(s -> s.getInReplyToStatusId() > 0)
		.map(s -> (Reply)ImmutableReply.builder()
				.id(String.valueOf(s.getInReplyToStatusId()))
				.count(1)
				.user(s.getInReplyToScreenName())
				.build())
        .buffer(DEF_BUFFER_SIZE)
        .subscribe(replyRepository::save);
	}
	
	
	
}
