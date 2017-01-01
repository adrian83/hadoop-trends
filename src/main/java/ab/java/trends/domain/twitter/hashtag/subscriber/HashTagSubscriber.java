package ab.java.trends.domain.twitter.hashtag.subscriber;

import ab.java.trends.domain.twitter.hashtag.HashtagFinder;
import ab.java.trends.domain.twitter.hashtag.repository.HashtagRepository;

import java.util.stream.Stream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import rx.Subscriber;
import twitter4j.Status;

@Component
public class HashTagSubscriber extends Subscriber<Status> {
    
    //private static final Logger LOGGER = LoggerFactory.getLogger(HashTagSubscriber.class);
    
    @Autowired
    private HashtagRepository hashtagRepository;
    
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
        
        Stream<String> hashtags = hashtagFinder.findHashtags(status.getText());
    
        hashtagRepository.updateHashtags(hashtags);
        
    }
    

    
    
    
}
