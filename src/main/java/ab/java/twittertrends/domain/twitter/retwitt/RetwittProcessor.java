package ab.java.twittertrends.domain.twitter.retwitt;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ab.java.twittertrends.domain.twitter.TwittsSource;
import ab.java.twittertrends.domain.twitter.retwitt.repository.RetwittRepository;

import reactor.core.publisher.Mono;
import twitter4j.Status;

@Component
public class RetwittProcessor {

	private static final Logger LOGGER = Logger.getLogger(RetwittProcessor.class.getSimpleName());
	
	@Autowired
	private TwittsSource twittsSource;
	
	@Autowired
	private RetwittRepository retwittRepository;
	
	
	@PostConstruct
	public void postCreate() {
		LOGGER.log(Level.INFO, "RetwittProcessor created");
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
		LOGGER.log(Level.INFO, "Starting persisting retwitts");
		
		twittsSource.twittsFlux()
		.filter(this::shouldProcess)
		.map(s -> (Retwitt)ImmutableRetwitt.builder()
				.id(String.valueOf(s.getRetweetedStatus().getId()))
				.retwitted(s.getRetweetedStatus().getRetweetCount())
				.user(s.getRetweetedStatus().getUser().getScreenName())
				.build())
        .map(retwittRepository::save)
        .map(Mono::block)
        .subscribe(ur -> LOGGER.log(Level.INFO, "Saved retwitt: {0}", ur.getUpsertedId()));  
        
	}
	
	
	
}
