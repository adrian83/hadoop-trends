package com.github.adrian83.trends.domain.favorite;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.github.adrian83.trends.common.web.SseController;

import reactor.core.publisher.Flux;

import static com.github.adrian83.trends.common.web.ViewController.SSE_CONTENT_TYPE;
import static com.github.adrian83.trends.common.web.ViewController.SSE_PATH;

@RestController
public class FavoriteController extends SseController<Favorite> {

	private static final String FAVORITES = "favorites";
	
	@Autowired
	private FavoriteService favoriteService;

	
	@GetMapping(value = SSE_PATH + FAVORITES, produces = SSE_CONTENT_TYPE)
	public Flux<ServerSentEvent<List<Favorite>>> sseFavorited() {
		return toSse(favoriteService.favorited());
	}
	
	@RequestMapping(value = "/view/" + FAVORITES)
	public String favorites() {
		return FAVORITES;
	}

}
