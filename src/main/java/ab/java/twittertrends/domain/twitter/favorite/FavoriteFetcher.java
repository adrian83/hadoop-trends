package ab.java.twittertrends.domain.twitter.favorite;

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
public class FavoriteFetcher {

	private static final Logger LOGGER = LoggerFactory.getLogger(FavoriteFetcher.class);

	@Autowired
	private FavoriteRepository favoriteRepository;
	
	private ConnectableObservable<List<Favorite>> favorites;
		
	@PostConstruct
	public void postCreate() {
		LOGGER.debug("Starting reading favorites");
		
		favorites = Observable.interval(5, TimeUnit.SECONDS)
				.flatMap(i -> favoriteRepository.favorites(10)		
						.map(hdl -> hdl.stream()
								.map(hd -> (Favorite)ImmutableFavorite.builder()
										.id(hd.getTwittId())
										.favorite(hd.getFaworite())
										.build())
								.collect(Collectors.toList())))
 				.publish();
 		
		favorites.connect();
	}
	
	public Observable<List<Favorite>> favorites() {
		return favorites;
	}
	
}
