package ab.java.trends.web.hashtag;

import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ab.java.trends.domain.twitter.hashtag.domain.Hashtag;
import ab.java.trends.domain.twitter.hashtag.repository.HashtagRepository;
import reactor.core.publisher.Flux;

@RestController
public class HashtagController {
	
	@Autowired
	private HashtagRepository hashtagRepository;

	@RequestMapping(value = "/hashtags/popular")
	public Flux<HashtagDto> starters() {
		
		Stream<Hashtag> tags = hashtagRepository.findMostPopular(10);
		
		Stream<HashtagDto> dts = tags.map(tag -> new HashtagDto(tag.getName().orElse("unknown"), tag.getCount().orElse(-1)));
		
		return Flux.fromStream(dts);//   .just(new HashtagDto("test", 111));
	}
	
}
