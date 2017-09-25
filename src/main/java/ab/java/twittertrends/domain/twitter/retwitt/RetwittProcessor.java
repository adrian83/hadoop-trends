package ab.java.twittertrends.domain.twitter.retwitt;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ab.java.twittertrends.domain.twitter.TwittsSource;
import ab.java.twittertrends.domain.twitter.retwitt.repository.RetwittRepository;

@Component
public class RetwittProcessor {

	private static final Logger LOGGER = Logger.getLogger(RetwittProcessor.class.getSimpleName());

	private static final int DEF_BUFFER_SIZE = 10;
	
	@Autowired
	private TwittsSource twittsSource;
	
	@Autowired
	private RetwittRepository retwittRepository;
	
	
	@PostConstruct
	public void postCreate() {
		LOGGER.log(Level.INFO, "RetwittProcessor created");
		persistRetwitts();
	}
	
	private void persistRetwitts() {
		LOGGER.log(Level.INFO, "Starting persisting retwitts");
		/*
		twittsSource.twitts()
		.filter(s -> s.getRetweetCount() > 0)
		.map(s -> (Retwitt)ImmutableRetwitt.builder()
				.id(s.getId())
				.retwitted(s.getRetweetCount())
				.build())
        .buffer(DEF_BUFFER_SIZE)
        .subscribe(retwittRepository::save);
        */
	}
	
	
	
}
