package ab.java.twittertrends.domain.twitter.hashtag;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.reactivestreams.Publisher;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import reactor.core.publisher.Flux;

@Component
public class HashtagFinder {
	private static final String HASHTAG_PATTERN_STR = "#[a-zA-Z0-9]{1,}";
	private static final Pattern HASHTAG_PATTERN = Pattern.compile(HASHTAG_PATTERN_STR);

	public Flux<String> findHashtags(String text) {
		
		return Flux.from(new Publisher<String>() {

			@Override
			public void subscribe(org.reactivestreams.Subscriber<? super String> subscriber) {
				if (StringUtils.isEmpty(text)) {
					subscriber.onComplete();
				}

				Matcher matcher = HASHTAG_PATTERN.matcher(text);

				while (matcher.find()) {
					subscriber.onNext(text.substring(matcher.start(), matcher.end()));
				}
				subscriber.onComplete();
			}
			
		});
	}

}
