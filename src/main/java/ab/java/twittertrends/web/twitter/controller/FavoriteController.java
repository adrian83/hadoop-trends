package ab.java.twittertrends.web.twitter.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import ab.java.twittertrends.domain.twitter.common.Service;
import ab.java.twittertrends.domain.twitter.favorite.Favorite;
import reactor.core.publisher.Flux;

@RestController
public class FavoriteController extends SseController<Favorite> {

	@Autowired 
	private Service<Favorite> favoriteFetcher;
	
	@GetMapping(value = SSE_PATH + FAVORITES, produces = SSE_CONTENT_TYPE)
	public Flux<ServerSentEvent<List<Favorite>>> sseFavorites() {
		return toSse(favoriteFetcher.elements());
	}
}
