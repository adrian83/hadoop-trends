package ab.java.twittertrends.domain.twitter.service;

import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ab.java.twittertrends.domain.twitter.hashtag.HashtagFinder;
import ab.java.twittertrends.domain.twitter.hashtag.subscriber.BatchHashTagSubscriber;
import ab.java.twittertrends.domain.twitter.hashtag.subscriber.HashTagSubscriber;

@Component
public class TwitterProcesser {

    @Autowired
    private HashTagSubscriber hashTagSubscriber;
    
    @Autowired
    private BatchHashTagSubscriber batchHashTagSubscriber;
    
    @Autowired
    private HashtagFinder hashtagFinder;

    @Autowired
    private TwitterService twitterService;

    @PostConstruct
    public void processTweets() {
        //twitterService.getTwitts().subscribe(hashTagSubscriber);
        
        twitterService.getTwitts()
        .map(s -> s.getText())
        .flatMap(text -> hashtagFinder.findHashtags(text))
        .buffer(100)
        .subscribe(batchHashTagSubscriber); 
        
    }

}
