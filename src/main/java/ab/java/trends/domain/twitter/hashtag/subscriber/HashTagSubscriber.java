package ab.java.trends.domain.twitter.hashtag.subscriber;

import ab.java.trends.domain.twitter.hashtag.HashtagFinder;
import ab.java.trends.domain.twitter.hashtag.repository.HashtagRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import rx.Subscriber;

import twitter4j.Status;

@Component
public class HashTagSubscriber extends Subscriber<Status> {

	private static final Logger LOGGER = LoggerFactory.getLogger(HashTagSubscriber.class);

	@Autowired
	private HashtagRepository hashtagRepository;

	@Autowired
	private HashtagFinder hashtagFinder;

	@Override
	public void onCompleted() {

	}

	@Override
	public void onError(Throwable e) {
		LOGGER.warn("Cannot read hashtag. Exception: {}", e);
	}

	@Override
	public void onNext(Status status) {
		hashtagFinder.findHashtags(status.getText()).subscribe(hashtagRepository.updateHashtags());
	}

}
