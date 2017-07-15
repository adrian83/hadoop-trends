package ab.java.twittertrends.domain.twitter.hashtag;



import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ab.java.twittertrends.domain.twitter.TwittsSource;

@Component
public class HashtagProcessor {

	private static final Logger LOGGER = LoggerFactory.getLogger(HashtagProcessor.class);

	@Autowired
	private TwittsSource twittsSource;
	
	@Autowired
	private HashtagFinder hashtagFinder;
	
	@Autowired
	private HashtagRepository mongoRepository;
	
	
	@PostConstruct
	public void postCreate() {
		LOGGER.debug("Starting processing hashtags");
		
		persistTwitts();
	}
	
	private void persistTwitts() {
		twittsSource.twitts()
		.map(s -> s.getText())
        .flatMap(text -> hashtagFinder.findHashtags(text))
        .buffer(500)
        .subscribe(hashtags -> {
        	List<Hashtag> tags = hashtags.stream()
        	.collect(Collectors.groupingBy(Function.identity(), Collectors.counting()))
        	.entrySet()
        	.stream().map(e -> ImmutableHashtag.builder()
        			.name(e.getKey())
        			.count(e.getValue())
        			.build())
        	.collect(Collectors.toList());
        	mongoRepository.save(tags);
        	
        });
	}
	
	
	
	
}
