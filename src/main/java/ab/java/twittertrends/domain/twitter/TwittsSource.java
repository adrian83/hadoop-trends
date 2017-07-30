package ab.java.twittertrends.domain.twitter;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ab.java.twittertrends.config.TwitterConfig;
import ab.java.twittertrends.domain.twitter.domain.TwitterAuth;
import rx.Observable;
import rx.Observable.OnSubscribe;
import rx.Subscriber;
import twitter4j.Status;
import twitter4j.StatusAdapter;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;
import twitter4j.auth.AccessToken;

@Service
public class TwittsSource {

	private static final Logger LOGGER = LoggerFactory.getLogger(TwittsSource.class);

	private TwitterStream twitterStream;

	@Autowired
	private TwitterConfig twitterConfig;

	@PostConstruct
	public void connect() {

		TwitterAuth auth = twitterConfig.getAuhentication();

		AccessToken accessToken = new AccessToken(auth.token(), auth.secret());

		twitterStream = new TwitterStreamFactory().getInstance();

		twitterStream.setOAuthConsumer(auth.customerKey(), auth.customerSecret());
		twitterStream.setOAuthAccessToken(accessToken);

		twitterStream.addListener(new StatusAdapter() {
			@Override
			public void onStatus(Status status) {
			}

			@Override
			public void onException(Exception ex) {
			}
		});

		twitterStream.sample();

		LOGGER.info("Twitter client connected");
	}

	@PreDestroy
	public void disconnect() {

		twitterStream.clearListeners();
		twitterStream.cleanUp();

		LOGGER.info("Twitter client disconnected");
	}

	public Observable<Status> twitts() {
		return Observable.unsafeCreate(new OnSubscribe<Status>() {
			public void call(Subscriber<? super Status> subscriber) {

				twitterStream.addListener(new StatusAdapter() {
					@Override
					public void onStatus(Status status) {
						subscriber.onNext(status);
					}

					@Override
					public void onException(Exception ex) {
						subscriber.onError(ex);
					}
				});

			}
		});
	}

}
