package ab.java.twittertrends.domain.twitter.common;

import java.util.List;

import com.mongodb.client.result.UpdateResult;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface Repository <T> {

	Flux<List<T>> take(int count);
	
	Mono<UpdateResult> save(T elem);
	
}
