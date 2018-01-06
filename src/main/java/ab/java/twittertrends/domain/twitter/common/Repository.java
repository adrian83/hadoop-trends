package ab.java.twittertrends.domain.twitter.common;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface Repository <T> {

	Flux<List<T>> take(int count);
	
	Mono<UpdateResult> save(T elem);
	
	Mono<DeleteResult> deleteOlderThan(long amount, TimeUnit unit);
	
	default long utcNow() {
		return ZonedDateTime.now(ZoneOffset.UTC).toEpochSecond();
	}

	default long utcNowMinus(long amount, TimeUnit unit) {
		long seconds = unit.toSeconds(amount);
		return ZonedDateTime.now(ZoneOffset.UTC).minusSeconds(seconds).toEpochSecond();
	}
	
}
