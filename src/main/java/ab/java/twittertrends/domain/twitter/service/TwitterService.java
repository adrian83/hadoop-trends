package ab.java.twittertrends.domain.twitter.service;

import rx.Observable;
import twitter4j.Status;

public interface TwitterService {
	
	Observable<Status> getTwitts();

}
