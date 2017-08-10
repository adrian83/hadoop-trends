package ab.java.twittertrends.domain.twitter.reply;

import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ab.java.twittertrends.domain.twitter.reply.repository.ReplyRepository;
import rx.Observable;
import rx.observables.ConnectableObservable;

@Component
public class ReplyFetcher {

	private static final Logger LOGGER = LoggerFactory.getLogger(ReplyFetcher.class);

	@Autowired
	private ReplyRepository replyRepository;
	
	private ConnectableObservable<List<Reply>> replies;
		
	@PostConstruct
	public void postCreate() {
		LOGGER.debug("Starting reading replies");
		
		replies = Observable.interval(5, TimeUnit.SECONDS)
				.flatMap(i -> replyRepository.replies(10))	
 				.publish();
 		
		replies.connect();
	}
	
	public Observable<List<Reply>> replies() {
		return replies;
	}
	
}
