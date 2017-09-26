package ab.java.twittertrends.domain.twitter.hashtag;

import java.time.Duration;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ab.java.twittertrends.domain.twitter.hashtag.repository.HashtagRepository;
import reactor.core.publisher.ConnectableFlux;
import reactor.core.publisher.Flux;

@Component
public class HashtagFetcher {

	private static final Logger LOGGER = Logger.getLogger(HashtagFetcher.class.getSimpleName());

	@Autowired
	private HashtagRepository hashtagRepository;
	
	private ConnectableFlux<List<Hashtag>> hashtags;
	
	
	@PostConstruct
	public void postCreate() {
		LOGGER.log(Level.INFO, "Created");
		
 		hashtags = Flux.interval(Duration.ofSeconds(10))
				.flatMap(i -> hashtagRepository.popularHashtags(10))
 				.publish();
 		
 		hashtags.connect();
 		LOGGER.log(Level.INFO, "Hot observable started");
	}
	
	public Flux<List<Hashtag>> hashtags() {
		return hashtags;
	}
	
}
