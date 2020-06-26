package com.github.adrian83.trends.domain.status;

import java.util.Optional;

import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
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

  public Flux<Status> twittsFlux() {
    return Flux.from(new StatusPublisher()).subscribeOn(Schedulers.parallel());
  }

  private class StatusPublisher implements Publisher<Status> {

    @Override
    public void subscribe(Subscriber<? super Status> subscriber) {
      twitterStream.addListener(new TrendsStatusAdapter(subscriber));
    }
  }

  private class TrendsStatusAdapter extends StatusAdapter {

    private Subscriber<? super Status> subscriber;

    TrendsStatusAdapter(Subscriber<? super Status> subscriber) {
      this.subscriber = subscriber;
    }

    @Override
    public void onStatus(Status status) {
      Optional.ofNullable(status)
          .filter(s -> subscriber != null)
          .ifPresent(
              s -> {
                //LOGGER.debug(s.toString());
                subscriber.onNext(s);
              });
    }

    @Override
    public void onException(Exception ex) {
      Optional.ofNullable(ex)
          .filter(s -> subscriber != null)
          .ifPresent(
              e -> {
                e.printStackTrace();
                subscriber.onError(e);
              });
    }
  }
}
