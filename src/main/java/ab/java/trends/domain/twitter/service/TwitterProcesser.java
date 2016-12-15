package ab.java.trends.domain.twitter.service;

import ab.java.trends.domain.twitter.subscriber.HashTagSubscriber;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TwitterProcesser {

    @Autowired
    private HashTagSubscriber hashTagSubscriber;

    @Autowired
    private TwitterService twitterService;

    @PostConstruct
    public void processTweets() {
        twitterService.getTwitts().subscribe(hashTagSubscriber);
    }

}
