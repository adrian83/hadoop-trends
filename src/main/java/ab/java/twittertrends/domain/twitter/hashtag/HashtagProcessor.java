package ab.java.twittertrends.domain.twitter.hashtag;

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
	private HashtagRepository hashtagRepository;
	
	@PostConstruct
	public void postCreate() {
		LOGGER.log(Level.INFO, "HashtagProcessor created");
		persistHashtags();
	}
	
	private void persistHashtags() {
		LOGGER.log(Level.INFO, "Starting persisting hashtags");
		twittsSource.twittsFlux()
		.map(Status::getText)
        .flatMap(hashtagFinder::findHashtags)
        .buffer(DEF_BUFFER_SIZE)
        .flatMapIterable(hashtags -> hashtags.stream()
        		.collect(Collectors.groupingBy(Function.identity(), Collectors.counting()))
        		.entrySet()
        		.stream()
        		.map(e -> (Hashtag) ImmutableHashtag.builder().name(e.getKey()).count(e.getValue()).build())
        		.collect(Collectors.toList()))
        .map(hashtagRepository::save)
        .subscribe(
        		mur -> mur.subscribe(ur -> LOGGER.log(Level.INFO, "Saved hashtag: {0}", ur.getUpsertedId())), 
        		t -> LOGGER.log(Level.INFO, "Exception during processing hashtags {0}", t));
		
	}
	
}
