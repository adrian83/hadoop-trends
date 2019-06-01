package com.github.adrian83.trends.domain.favorite.logic;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.github.adrian83.trends.common.Repository;
import com.github.adrian83.trends.domain.favorite.logic.FavoriteService;
import com.github.adrian83.trends.domain.favorite.model.FavoriteDoc;
import com.github.adrian83.trends.domain.favorite.model.FavoriteMapper;
import com.github.adrian83.trends.domain.status.StatusSource;
import com.github.adrian83.trends.domain.status.TestStatus;
import com.github.adrian83.trends.domain.status.TestUser;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import twitter4j.Status;
import twitter4j.User;

@RunWith(MockitoJUnitRunner.class)
public class FavoriteServiceTest {

  @InjectMocks private FavoriteService favoriteService;

  @Mock private StatusSource statusSourceMock;
  @Mock private Repository<FavoriteDoc> favoriteRepositoryMock;
  @Mock private FavoriteMapper favoriteMapperMock;

  @Test
  public void shouldStartPersistingFavorites() {
    // given
    Flux<Status> statuses = generate(1);
    String id = "abc-def";

    when(statusSourceMock.twittsFlux()).thenReturn(statuses);
    when(favoriteRepositoryMock.save(any(FavoriteDoc.class))).thenReturn(Mono.just(id));
    when(favoriteRepositoryMock.top(anyInt())).thenReturn(Flux.empty());

    // when
    favoriteService.postCreate();

    // then
    verify(statusSourceMock).twittsFlux();
    verify(favoriteRepositoryMock).save(any(FavoriteDoc.class));
  }
  /*
  @Test
  public void shouldPersistValidStatus() {
  	// given
  	TestStatus retweetedStatus = TestStatus.builder()
  			.id(88)
  			.favoriteCount(20)
  			.user(TestUser.builder().screenName("John007").build())
  			.build();

  	TestStatus status = TestStatus.builder()
  			.id(1321l)
  			.retweetedStatus(retweetedStatus)
  			.build();

  	Flux<Status> statusFlux = Flux.just(status);

  	UpdateResult updateResult = mock(UpdateResult.class);

  	when(twittsSourceMock.twittsFlux()).thenReturn(statusFlux);
  	when(favoriteRepositoryMock.save(any(Favorite.class))).thenReturn(Mono.just(updateResult));
  	when(updateResult.getUpsertedId()).thenReturn(new BsonString("abc"));

  	// when
  	favoriteProcessor.postCreate();

  	// then
  	verify(twittsSourceMock).twittsFlux();
  	verify(favoriteRepositoryMock).save(any(Favorite.class));
  }

  @Test
  public void shouldFilterOutStatusesWithoutRetwitedStatus() {
  	// given
  	TestStatus status = TestStatus.builder()
  			.id(1321l)
  			.build();

  	Flux<Status> statusFlux = Flux.just(status);

  	when(twittsSourceMock.twittsFlux()).thenReturn(statusFlux);

  	// when
  	favoriteProcessor.postCreate();

  	// then
  	verify(twittsSourceMock).twittsFlux();
  	verify(favoriteRepositoryMock, never()).save(any(Favorite.class));
  }

  @Test
  public void shouldFilterOutStatusesWithFavoriteCountBelowOne() {
  	// given
  	TestStatus retweetedStatus = TestStatus.builder()
  			.id(88)
  			.favoriteCount(0)
  			.user(TestUser.builder().screenName("John007").build())
  			.build();

  	TestStatus status = TestStatus.builder()
  			.id(1321l)
  			.retweetedStatus(retweetedStatus)
  			.build();


  	Flux<Status> statusFlux = Flux.just(status);

  	when(twittsSourceMock.twittsFlux()).thenReturn(statusFlux);

  	// when
  	favoriteProcessor.postCreate();

  	// then
  	verify(twittsSourceMock).twittsFlux();
  	verify(favoriteRepositoryMock, never()).save(any(Favorite.class));
  }

  @Test
  public void shouldFilterOutStatusesWithIdBelowOne() {
  	// given
  	TestStatus retweetedStatus = TestStatus.builder()
  			.id(0)
  			.favoriteCount(10)
  			.user(TestUser.builder().screenName("John007").build())
  			.build();

  	TestStatus status = TestStatus.builder()
  			.id(1321l)
  			.retweetedStatus(retweetedStatus)
  			.build();


  	Flux<Status> statusFlux = Flux.just(status);

  	when(twittsSourceMock.twittsFlux()).thenReturn(statusFlux);

  	// when
  	favoriteProcessor.postCreate();

  	// then
  	verify(twittsSourceMock).twittsFlux();
  	verify(favoriteRepositoryMock, never()).save(any(Favorite.class));
  }

  @Test
  public void shouldFilterOutStatusesWithoutUser() {
  	// given
  	TestStatus retweetedStatus = TestStatus.builder()
  			.id(10)
  			.favoriteCount(10)
  			.build();

  	TestStatus status = TestStatus.builder()
  			.id(1321l)
  			.retweetedStatus(retweetedStatus)
  			.build();


  	Flux<Status> statusFlux = Flux.just(status);

  	when(twittsSourceMock.twittsFlux()).thenReturn(statusFlux);

  	// when
  	favoriteProcessor.postCreate();

  	// then
  	verify(twittsSourceMock).twittsFlux();
  	verify(favoriteRepositoryMock, never()).save(any(Favorite.class));
  }

  @Test
  public void shouldFilterOutStatusesWithoutUsersScreenName() {
  	// given
  	TestStatus retweetedStatus = TestStatus.builder()
  			.id(20)
  			.favoriteCount(10)
  			.user(TestUser.builder().build())
  			.build();

  	TestStatus status = TestStatus.builder()
  			.id(1321l)
  			.retweetedStatus(retweetedStatus)
  			.build();


  	Flux<Status> statusFlux = Flux.just(status);

  	when(twittsSourceMock.twittsFlux()).thenReturn(statusFlux);

  	// when
  	favoriteProcessor.postCreate();

  	// then
  	verify(twittsSourceMock).twittsFlux();
  	verify(favoriteRepositoryMock, never()).save(any(Favorite.class));
  }
  */

  private Status generateStatus(long id, String username, String text, int retwittedCount) {
    User user = new TestUser(username + "_" + id);
    return new TestStatus(id, text, retwittedCount, user);
  }

  private Status generateStatusWithRetwitt(
      long id, String username, String text, int retwittedCount) {
    Status retwitt = generateStatus(id + 100, "john", "Some test", retwittedCount + 100);
    User user = new TestUser(username + "_" + id);
    return new TestStatus(id, text, retwittedCount, user).withRetweetedStatus(retwitt);
  }

  private Flux<Status> generate(int length) {
    Stream<Status> stream =
        IntStream.range(0, length).mapToObj(i -> generateStatusWithRetwitt(i, "linda", "Text", i));
    return Flux.fromStream(stream);
  }
}
