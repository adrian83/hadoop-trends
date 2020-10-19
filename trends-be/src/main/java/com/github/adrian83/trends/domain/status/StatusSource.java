package com.github.adrian83.trends.domain.status;

import javax.annotation.PreDestroy;

import org.reactivestreams.Publisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;
import twitter4j.Status;
import twitter4j.StatusAdapter;
import twitter4j.TwitterStream;

@Service
public class StatusSource {

  private static final Logger LOGGER = LoggerFactory.getLogger(StatusSource.class);

  @Autowired private TwitterStream twitterStream;

  @PreDestroy
  public void disconnect() {

    twitterStream.clearListeners();
    twitterStream.cleanUp();

    LOGGER.info("Twitter client disconnected");
  }

  public Flux<Status> twittsFlux() {

    return Flux.from(
            new Publisher<Status>() {

              @Override
              public void subscribe(org.reactivestreams.Subscriber<? super Status> subscriber) {

                twitterStream.addListener(
                    new StatusAdapter() {
                      @Override
                      public void onStatus(Status status) {
                        LOGGER.debug(status.toString());
                        subscriber.onNext(status);
                      }

                      @Override
                      public void onException(Exception ex) {
                        ex.printStackTrace();
                        subscriber.onError(ex);
                      }
                    });
              }
            })
        .subscribeOn(Schedulers.parallel());
  }
}
