package ab.java.twittertrends.domain.common;

import reactor.core.publisher.Flux;
import rx.Observable;
import rx.Observable.OnSubscribe;
import rx.Subscriber;

public class Observables {

	public static <T> Observable<T> fromFlux(Flux<T> flux) {
		return Observable.create(new OnSubscribe<T>() {
			public void call(Subscriber<? super T> subscriber) {
				flux.subscribe(l -> subscriber.onNext(l), e -> subscriber.onError(e));
			}
		});
	}

}
