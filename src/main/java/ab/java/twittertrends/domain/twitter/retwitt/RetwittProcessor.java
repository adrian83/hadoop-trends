package ab.java.twittertrends.domain.twitter.retwitt;


import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ab.java.twittertrends.domain.twitter.TwittsSource;
import ab.java.twittertrends.domain.twitter.retwitt.repository.RetwittRepository;

import twitter4j.Status;

@Component
public class RetwittProcessor {

	private static final Logger LOGGER = LoggerFactory.getLogger(RetwittProcessor.class);
	
	@Autowired
	private TwittsSource twittsSource;
	
	@Autowired
	private RetwittRepository retwittRepository;
	
	
	@PostConstruct
	public void postCreate() {
		LOGGER.info("RetwittProcessor created");
		persistRetwitts();
	}
	
	private boolean shouldProcess(Status status) {
		return status.getRetweetedStatus() != null 
				&& status.getRetweetedStatus().getId() > 0
				&& status.getRetweetedStatus().getUser() != null 
				&& status.getRetweetedStatus().getUser().getScreenName() != null 
				&& status.getRetweetedStatus().getRetweetCount() > 0;
	}
	
	private void persistRetwitts() {
		LOGGER.info("Starting persisting retwitts");
		
		twittsSource.twittsFlux()
		.filter(this::shouldProcess)
		.map(s -> (Retwitt)ImmutableRetwitt.builder()
				.id(String.valueOf(s.getRetweetedStatus().getId()))
				.retwitted(s.getRetweetedStatus().getRetweetCount())
				.user(s.getRetweetedStatus().getUser().getScreenName())
				.build())
        .map(retwittRepository::save)
		.subscribe(mur -> mur.subscribe(ur -> LOGGER.info("Saved retwitt: {}", ur.getUpsertedId())));          
	}
	
	
	
}
