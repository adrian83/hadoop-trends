package ab.java.trends.domain.twitter.service;

import rx.Observable;

public interface TwitterService {
	
	Observable<String> getTwitts();

}
