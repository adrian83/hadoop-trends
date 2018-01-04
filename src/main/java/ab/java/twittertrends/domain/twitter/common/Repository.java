package ab.java.twittertrends.domain.twitter.common;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.List;

import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface Repository <T> {

	Flux<List<T>> take(int count);
	
	Mono<UpdateResult> save(T elem);
	
	Mono<DeleteResult> deleteOlderThan(LocalDateTime time);
	
	default LocalDateTime utcNow() {
		return ZonedDateTime.now(ZoneOffset.UTC).toLocalDateTime();
	}

}
