package ab.java.twittertrends.domain.twitter.hashtag.subscriber;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Component;

import rx.Subscriber;


@Component
public class BatchHashTagSubscriber extends Subscriber<List<String>> {

	private static final Logger LOGGER = LoggerFactory.getLogger(HashTagSubscriber.class);



	@Override
	public void onCompleted() {

	}

	@Override
	public void onError(Throwable e) {
		LOGGER.warn("Cannot read hashtag. Exception: {}", e);
	}

	@Override
	public void onNext(List<String> statuses) {
		Map<String, Long> gruppedHashTags = statuses.stream()
				.collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
		System.out.println(gruppedHashTags);
	}

}
