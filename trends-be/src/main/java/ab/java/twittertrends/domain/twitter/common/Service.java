package ab.java.twittertrends.domain.twitter.common;

import java.util.List;

import reactor.core.publisher.Flux;

public interface Service <T> {
	
	long CLEANING_FIXED_RATE_MS = 60000; 
	long CLEANING_INITIAL_DELAY_MS = 120000;

	Flux<List<T>> elements();
	
	void removeUnused();
	
}
