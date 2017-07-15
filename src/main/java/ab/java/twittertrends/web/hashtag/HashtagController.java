package ab.java.twittertrends.web.hashtag;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import rx.Observable;

@RestController
public class HashtagController {
	



	@RequestMapping(value = "/hashtags/popular")
	public Observable<HashtagDto> starters() {
		
		return null;
	}
	
}


