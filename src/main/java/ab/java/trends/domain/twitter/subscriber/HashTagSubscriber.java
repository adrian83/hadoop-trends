package ab.java.trends.domain.twitter.subscriber;

import ab.java.trends.domain.rethink.repository.RethinkRepository;
import ab.java.trends.domain.twitter.hashtag.HashtagFinder;
import java.util.stream.Stream;
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
    private RethinkRepository rethinkRepository;
    
    @Autowired
    private HashtagFinder hashtagFinder;

    @Override
    public void onCompleted() {

    }

    @Override
    public void onError(Throwable e) {

    }

    @Override
    public void onNext(Status status) {
        //LOGGER.warn("Reading tags from {}",status.getText());
        
        Stream<String> hashtags = hashtagFinder.findHashtags(status.getText());
 
        //LOGGER.warn("text: {}", status.getText());
                
        rethinkRepository.updateHashtags(hashtags);
        
    }
    

    
    
    
}
