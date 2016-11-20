package ab.java.hadoop.trends.domain.twitter.service.impl;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.twitter.hbc.ClientBuilder;
import com.twitter.hbc.core.Constants;
import com.twitter.hbc.core.endpoint.StatusesSampleEndpoint;
import com.twitter.hbc.core.processor.StringDelimitedProcessor;
import com.twitter.hbc.httpclient.BasicClient;
import com.twitter.hbc.httpclient.auth.Authentication;

import ab.java.hadoop.trends.config.TwitterConfig;
import ab.java.hadoop.trends.domain.twitter.service.TwitterService;
import rx.Observable;
import rx.Observable.OnSubscribe;
import rx.Observer;
import rx.Subscriber;

@Service
public class TwitterServiceImpl implements TwitterService {

	private static final Logger LOGGER = LoggerFactory.getLogger(TwitterServiceImpl.class);

	public static final long TIMEOUT = 5;

	@Autowired
	private TwitterConfig config;

	private BlockingQueue<String> queue;
	private BasicClient client;

	@PostConstruct
	public void connect() {
		queue = new LinkedBlockingQueue<String>(10000);

		StatusesSampleEndpoint endpoint = new StatusesSampleEndpoint();
		endpoint.stallWarnings(false);

		Authentication auth = config.getAuhentication();

		client = new ClientBuilder()
				.name("sampleExampleClient")
				.hosts(Constants.STREAM_HOST)
				.endpoint(endpoint)
				.authentication(auth)
				.processor(new StringDelimitedProcessor(queue))
				.build();

		client.connect();
		
		LOGGER.info("Twitter client connected");
	}
	
	@PreDestroy
	public void disconnect() {
		client.stop();
		
		LOGGER.info("Twitter client disconnected");
	}

	@Override
	public Observable<String> getTwitts() {

		final OnSubscribe<String> ms = new MyOnSubscribe(client, queue);

		return Observable.create(ms);


	}

}

class MyOnSubscribe implements OnSubscribe<String> {

	private BasicClient client;
	private BlockingQueue<String> queue;

	public MyOnSubscribe(BasicClient client, BlockingQueue<String> queue) {
		this.client = client;
		this.queue = queue;
	}

	@Override
	public void call(Subscriber<? super String> t) {
		if (t.isUnsubscribed()) {
			return;
		}

		while (true) {
			if (client.isDone()) {
				t.onCompleted();
				break;
			}

			try {
				String msg = queue.poll(TwitterServiceImpl.TIMEOUT, TimeUnit.SECONDS);
				if (msg == null) {
					t.onError(new Exception("Did not receive a message in 5 seconds"));
				} else {
					t.onNext(msg);
				}
			} catch (InterruptedException e) {
				t.onError(e);
			}

		}

		if (!t.isUnsubscribed()) {
			t.onCompleted();
		}
	}

}
