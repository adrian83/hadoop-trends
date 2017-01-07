package ab.java.trends.domain.twitter.hashtag;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import rx.Observable;
import rx.Subscriber;
import rx.Observable.OnSubscribe;

@Component
public class HashtagFinder {
	private static final String HASHTAG_PATTERN_STR = "#[a-zA-Z0-9]{1,}";
	private static final Pattern HASHTAG_PATTERN = Pattern.compile(HASHTAG_PATTERN_STR);

	public Observable<String> findHashtags(String text) {

		return Observable.create(new OnSubscribe<String>() {

			@Override
			public void call(Subscriber<? super String> subscriber) {

				if (StringUtils.isEmpty(text)) {
					subscriber.onCompleted();
				}

				Matcher matcher = HASHTAG_PATTERN.matcher(text);

				while (matcher.find()) {
					subscriber.onNext(text.substring(matcher.start(), matcher.end()));
				}
				subscriber.onCompleted();
			}
		});
	}

}
