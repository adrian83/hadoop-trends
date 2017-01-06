package ab.java.trends.web.hashtag;

import java.util.concurrent.CompletionStage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ab.java.trends.domain.twitter.hashtag.repository.HashtagRepository;

import reactor.core.publisher.Mono;

@RestController
public class HashtagController {
	
	@Autowired
	private HashtagRepository hashtagRepository;

	@RequestMapping(value = "/hashtags/popular")
	public Mono<PopularHashtagsDto> starters() {
		
		CompletionStage<PopularHashtagsDto> futurePopularTagsDto = hashtagRepository
				.findMostPopular(10)
				.thenApply(t -> t.map(ph -> new PopularHashtagsDto(ph.getTime(), ph.getHashtags())));
		
		return Mono.fromFuture(futurePopularTagsDto.toCompletableFuture());
	}
	
}
