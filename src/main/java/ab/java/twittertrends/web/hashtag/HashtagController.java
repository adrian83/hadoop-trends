package ab.java.twittertrends.web.hashtag;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ab.java.twittertrends.domain.twitter.hashtag.repository.HashtagRepository;

import rx.Observable;

@RestController
public class HashtagController {
	
	@Autowired
	private HashtagRepository hashtagRepository;


	@RequestMapping(value = "/hashtags/popular")
	public Observable<HashtagDto> starters() {
		
		return hashtagRepository
				.findMostPopular(10)
				.map(ht -> new HashtagDto(ht.getName(), ht.getCount()));
	}
	
}


