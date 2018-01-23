package ab.java.twittertrends.domain.twitter;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.reactivestreams.Publisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ab.java.twittertrends.config.TwitterConfig;
import ab.java.twittertrends.domain.twitter.common.TwitterAuth;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;
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
		
		LOGGER.info("Twitter client conecting");

		TwitterAuth auth = twitterConfig.getAuhentication();
		
		LOGGER.info("Twitter client conecting. Auth: {}", auth);

		AccessToken accessToken = new AccessToken(auth.getToken(), auth.getSecret());

		twitterStream = new TwitterStreamFactory().getInstance();

		twitterStream.setOAuthConsumer(auth.getCustomerKey(), auth.getCustomerSecret());
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

	public Flux<Status> twittsFlux() {

		return Flux.from(new Publisher<Status>() {

			@Override
			public void subscribe(org.reactivestreams.Subscriber<? super Status> subscriber) {

				twitterStream.addListener(new StatusAdapter() {
					@Override
					public void onStatus(Status status) {
						LOGGER.debug(status.toString());
						subscriber.onNext(status);
					}

					@Override
					public void onException(Exception ex) {
						ex.printStackTrace();
						// subscriber.onError(ex);
					}
				});
			}
		}).subscribeOn(Schedulers.parallel());
	}

}
