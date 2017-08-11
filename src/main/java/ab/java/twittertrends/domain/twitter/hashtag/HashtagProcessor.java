package ab.java.twittertrends.domain.twitter.hashtag;

import java.util.List;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ab.java.twittertrends.domain.twitter.TwittsSource;
import ab.java.twittertrends.domain.twitter.hashtag.repository.HashtagRepository;
import twitter4j.Status;


@Component
public class HashtagProcessor {

	private static final Logger LOGGER = Logger.getLogger(HashtagProcessor.class.getSimpleName());

	private static final int DEF_BUFFER_SIZE = 100;
	
	@Autowired
	private TwittsSource twittsSource;
	
	@Autowired
	private HashtagFinder hashtagFinder;
	
	@Autowired
	private HashtagRepository mongoRepository;
	
	
	@PostConstruct
	public void postCreate() {
		LOGGER.log(Level.INFO, "HashtagProcessor created");
		persistHashtags();
	}
	
	private void persistHashtags() {
		LOGGER.log(Level.INFO, "Starting persisting hashtags");
		
		twittsSource.twitts()
		.map(Status::getText)
        .flatMap(hashtagFinder::findHashtags)
        .buffer(DEF_BUFFER_SIZE)
        .subscribe(hashtags -> {
        	LOGGER.log(Level.INFO, String.format("Persisting %s hashtags", hashtags.size()));
        	
        	List<Hashtag> tags = hashtags.stream()
        	.collect(Collectors.groupingBy(Function.identity(), Collectors.counting()))
        	.entrySet()
        	.stream()
        	.map(e -> ImmutableHashtag.builder()
        			.name(e.getKey())
        			.count(e.getValue())
        			.build())
        	.collect(Collectors.toList());
        	
        	mongoRepository.save(tags);
        	
        });
	}
	
	
	
	
}
