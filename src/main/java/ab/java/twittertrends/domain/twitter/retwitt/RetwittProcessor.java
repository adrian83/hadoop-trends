package ab.java.twittertrends.domain.twitter.retwitt;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ab.java.twittertrends.domain.twitter.TwittsSource;

@Component
public class RetwittProcessor {

	private static final Logger LOGGER = LoggerFactory.getLogger(RetwittProcessor.class);

	@Autowired
	private TwittsSource twittsSource;
	
	@Autowired
	private RetwittRepository retwittRepository;
	
	
	@PostConstruct
	public void postCreate() {
		LOGGER.debug("Starting processing retwitts");
		
		persistTwitts();
	}
	
	private void persistTwitts() {
		twittsSource.twitts()
		.filter(s -> s.getRetweetCount() > 0)
		.map(s -> (Retwitt)ImmutableRetwitt.builder()
				.id(s.getId())
				.retwitted(s.getRetweetCount())
				.build())
        .buffer(2)
        .subscribe(retwittRepository::save);
	}
	
	
	
}
