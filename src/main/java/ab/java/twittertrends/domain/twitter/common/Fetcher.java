package ab.java.twittertrends.domain.twitter.common;

import java.util.List;

import reactor.core.publisher.Flux;

public interface Fetcher <T> {

	Flux<List<T>> elements();
	
	public void removeUnused();
	
}
