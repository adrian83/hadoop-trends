package ab.java.twittertrends.domain.twitter.reply;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ab.java.twittertrends.domain.twitter.reply.repository.ReplyRepository;
import rx.Observable;
import rx.observables.ConnectableObservable;

@Component
public class ReplyFetcher {

	private static final Logger LOGGER = Logger.getLogger(ReplyFetcher.class.getSimpleName());

	@Autowired
	private ReplyRepository replyRepository;
	
	private ConnectableObservable<List<Reply>> replies;
		
	@PostConstruct
	public void postCreate() {
		LOGGER.log(Level.INFO, "Created");
		
		replies = Observable.interval(5, TimeUnit.SECONDS)
				.flatMap(i -> replyRepository.mostReplied(10))	
 				.publish();
 		
		replies.connect();
		LOGGER.log(Level.INFO, "Hot observable started");
	}
	
	public Observable<List<Reply>> replies() {
		return replies;
	}
	
}
