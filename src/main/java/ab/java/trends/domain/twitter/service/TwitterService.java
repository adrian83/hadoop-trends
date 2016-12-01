package ab.java.trends.domain.twitter.service;

import rx.Observable;
import twitter4j.Status;

public interface TwitterService {
	
	Observable<Status> getTwitts();

}
