package ab.java.hadoop.trends.domain.twitter.service;

import rx.Observable;

public interface TwitterService {
	
	Observable<String> getTwitts();

}
