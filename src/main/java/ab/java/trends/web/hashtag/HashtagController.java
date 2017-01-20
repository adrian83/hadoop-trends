package ab.java.trends.web.hashtag;

import java.util.concurrent.CompletionStage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ab.java.trends.domain.twitter.hashtag.domain.Hashtag;
import ab.java.trends.domain.twitter.hashtag.domain.PopularHashtags;
import ab.java.trends.domain.twitter.hashtag.repository.HashtagRepository;

import reactor.core.publisher.Mono;
import rx.Observable;

@RestController
public class HashtagController {
	
	@Autowired
	private HashtagRepository hashtagRepository;

	@RequestMapping(value = "/test")
	public Observable<HashtagDto> test() {
		return Observable.just(new HashtagDto("test",2),new HashtagDto("test2",4));

	}
	
	@RequestMapping(value = "/hashtags/popular")
	public Observable<HashtagDto> starters() {
		
		return hashtagRepository
		.findMostPopular(10).map(h -> new HashtagDto(h.getName().orElse("asda"), h.getCount().orElse(-1)));
		
	}
	
}


