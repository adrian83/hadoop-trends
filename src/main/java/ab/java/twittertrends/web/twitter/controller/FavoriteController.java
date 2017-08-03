package ab.java.twittertrends.web.twitter.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ab.java.twittertrends.domain.twitter.favorite.Favorite;
import ab.java.twittertrends.domain.twitter.favorite.FavoriteFetcher;
import rx.Observable;

@RestController
public class FavoriteController {

	@Autowired 
	private FavoriteFetcher favoriteFetcher;
	
	@RequestMapping(value = "/favorites")
	public Observable<List<Favorite>> favorites() {
		return favoriteFetcher.favorites().first();
	}
	
	@GetMapping(value = "/sse/favorites", produces = "text/event-stream")
	public Observable<ServerSentEvent<List<Favorite>>> sseFavorites() {
		return favoriteFetcher.favorites().map(l -> ServerSentEvent.builder(l).build());
	}
}
