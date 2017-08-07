package ab.java.twittertrends.domain.twitter.reply;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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
				.flatMap(i -> replyRepository.replies(10)		
						.map(hdl -> hdl.stream()
								.map(hd -> (Reply)ImmutableReply.builder()
										.id(hd.getTwittId().toString())
										.user(hd.getUser())
										.count(hd.getCount())
										.build())
								.collect(Collectors.toList())))
 				.publish();
 		
		replies.connect();
	}
	
	public Observable<List<Reply>> replies() {
		return replies;
	}
	
}
