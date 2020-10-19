package com.github.adrian83.trends.domain.favorite.logic;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.test.util.ReflectionTestUtils;

import com.github.adrian83.trends.domain.common.Repository;
import com.github.adrian83.trends.domain.favorite.model.Favorite;
import com.github.adrian83.trends.domain.favorite.model.FavoriteDoc;
import com.github.adrian83.trends.domain.favorite.model.FavoriteMapper;
import com.github.adrian83.trends.domain.status.StatusSource;
import com.github.adrian83.trends.domain.status.TestStatus;
import com.github.adrian83.trends.domain.status.TestUser;

import reactor.core.publisher.Flux;
import twitter4j.Status;
import twitter4j.User;

@RunWith(MockitoJUnitRunner.class)
public class FavoriteServiceTest {

	@InjectMocks
	private FavoriteService favoriteService;

	@Mock
	private StatusSource statusSourceMock;
	@Mock
	private Repository<FavoriteDoc> favoriteRepositoryMock;
	@Mock
	private FavoriteMapper favoriteMapperMock;

	@Test
	public void shouldStartPersistingFavorites() {
		// given
		Flux<Status> statuses = generate(1);

		when(statusSourceMock.twittsFlux()).thenReturn(statuses);

		// when
		favoriteService.postCreate();

		// then
		verify(statusSourceMock).twittsFlux();
	}

	@Test
	public void shouldReturnMostFavoritedTwitts() throws InterruptedException {
		// given
		ReflectionTestUtils.setField(favoriteService, "readCount", 10);
		ReflectionTestUtils.setField(favoriteService, "readIntervalSec", 1);

		List<FavoriteDoc> favorites1 = generateFavorites(1);
		
		Favorite f = new Favorite();

		Mockito.when(favoriteRepositoryMock.top(Mockito.anyInt())).thenReturn(Flux.just(favorites1));
		Mockito.when(favoriteMapperMock.docToDto(Mockito.any(FavoriteDoc.class))).thenReturn(f);

		Thread.sleep(1500);
		
		// when
		favoriteService.top().subscribe((favorities) -> {
			assertThat(favorities.size(), equalTo(1));
		});

	}

	List<FavoriteDoc> generateFavorites(int count) {
		return LongStream.range(0, count).mapToObj(i -> new FavoriteDoc(i, "John-" + i, i, i))
				.collect(Collectors.toList());
	}

	private Status generateStatus(long id, String username, String text, int retwittedCount) {
		User user = new TestUser(username + "_" + id);
		return new TestStatus(id, text, retwittedCount, user);
	}

	private Status generateStatusWithRetwitt(long id, String username, String text, int retwittedCount) {
		Status retwitt = generateStatus(id + 100, "john", "Some test", retwittedCount + 100);
		User user = new TestUser(username + "_" + id);
		return new TestStatus(id, text, retwittedCount, user).withRetweetedStatus(retwitt);
	}

	private Flux<Status> generate(int length) {
		Stream<Status> stream = IntStream.range(0, length)
				.mapToObj(i -> generateStatusWithRetwitt(i, "linda", "Text", i));
		return Flux.fromStream(stream);
	}
}
