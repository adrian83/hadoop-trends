package ab.java.twittertrends.domain.twitter.reply;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ab.java.twittertrends.domain.twitter.TwittsSource;

@Component
public class ReplyProcessor {

	private static final Logger LOGGER = LoggerFactory.getLogger(ReplyProcessor.class);

	@Autowired
	private TwittsSource twittsSource;
	
	@Autowired
	private ReplyRepository replyRepository;
	
	
	@PostConstruct
	public void postCreate() {
		LOGGER.debug("Starting processing retwitts");
		
		persistTwitts();
	}
	
	private void persistTwitts() {
		twittsSource.twitts()
		.filter(s -> s.getInReplyToStatusId() > 0)
		.map(s -> (Reply)ImmutableReply.builder()
				.id(String.valueOf(s.getInReplyToStatusId()))
				.count(1)
				.user(s.getInReplyToScreenName())
				.build())
        .buffer(50)
        .subscribe(replyRepository::save);
	}
	
	
	
}
