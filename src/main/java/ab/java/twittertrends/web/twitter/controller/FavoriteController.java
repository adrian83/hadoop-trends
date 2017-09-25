package ab.java.twittertrends.web.twitter.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ab.java.twittertrends.domain.twitter.favorite.Favorite;
import ab.java.twittertrends.domain.twitter.favorite.FavoriteFetcher;
import reactor.core.publisher.Flux;

@RestController
public class FavoriteController {

	@Autowired 
	private FavoriteFetcher favoriteFetcher;
	
	@RequestMapping(value = "/favorites")
	public Flux<List<Favorite>> favorites() {
		return favoriteFetcher.favorites().take(1);
	}
	
	@GetMapping(value = "/sse/favorites", produces = "text/event-stream")
	public Flux<ServerSentEvent<List<Favorite>>> sseFavorites() {
		return favoriteFetcher.favorites().map(l -> ServerSentEvent.builder(l).build());
	}
}
