package ab.java.twittertrends.domain.twitter.favorite;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ab.java.twittertrends.domain.twitter.favorite.repository.FavoriteRepository;
import rx.Observable;
import rx.observables.ConnectableObservable;

@Component
public class FavoriteFetcher {

	private static final Logger LOGGER = Logger.getLogger(FavoriteProcessor.class.getSimpleName());

	@Autowired
	private FavoriteRepository favoriteRepository;
	
	private ConnectableObservable<List<Favorite>> favorites;
		
	@PostConstruct
	public void postCreate() {
		LOGGER.log(Level.INFO, "Created");
		
		favorites = Observable.interval(5, TimeUnit.SECONDS)
				.flatMap(i -> favoriteRepository.mostFavorited(10))
 				.publish();
 		
		favorites.connect();
		LOGGER.log(Level.INFO, "Hot observable started");
	}
	
	public Observable<List<Favorite>> favorites() {
		return favorites;
	}
	
}
