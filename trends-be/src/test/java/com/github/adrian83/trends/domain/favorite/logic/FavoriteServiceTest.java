package com.github.adrian83.trends.domain.favorite.logic;

import static com.github.adrian83.trends.common.Time.utcNow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.concurrent.TimeUnit;

import org.assertj.core.util.Lists;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.github.adrian83.trends.domain.common.Repository;
import com.github.adrian83.trends.domain.favorite.model.Favorite;
import com.github.adrian83.trends.domain.favorite.model.FavoriteDoc;
import com.github.adrian83.trends.domain.favorite.model.FavoriteMapper;
import com.github.adrian83.trends.domain.status.TestStatus;
import com.github.adrian83.trends.domain.status.TestUser;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import twitter4j.Status;
import twitter4j.User;

@RunWith(MockitoJUnitRunner.class)
public class FavoriteServiceTest {

  @InjectMocks private FavoriteService favoriteService;

  @Mock private Repository<FavoriteDoc> favoriteRepositoryMock;
  @Mock private FavoriteMapper favoriteMapperMock;

  @Test
  public void canaryTest() {}

  @Test
  public void shouldSaveFavorites() {
    // given
    User retweetedStatusUser = TestUser.builder().screenName("Mary").build();
    var retweetedStatus =
        TestStatus.builder()
            .favoriteCount(24)
            .id(123)
            .text("this is test")
            .user(retweetedStatusUser)
            .build();

    var validStatusUser = TestUser.builder().screenName("John").build();
    var validStatus =
        TestStatus.builder()
            .favoriteCount(24)
            .id(123)
            .text("this is test")
            .user(validStatusUser)
            .retweetedStatus(retweetedStatus)
            .build();

    Flux<Status> statusses = Flux.just(validStatus);

    when(favoriteRepositoryMock.save(any(FavoriteDoc.class))).thenReturn(Mono.just("abc-def-ghi"));

    // when
    favoriteService.processStatusses(statusses);

    // then
    verify(favoriteRepositoryMock, times(1)).save(any(FavoriteDoc.class));
  }

  @Test
  public void shouldFilterOutInvalidFavoritesBeforeSaving() {
    // given
    var userWithoutScreenName = TestUser.builder().build();
    var user = TestUser.builder().screenName("John").build();

    var statusWithInvalidFavoriteCount =
        TestStatus.builder().favoriteCount(-24).id(123).text("this is test").user(user).build();

    var statusWithInvalidId =
        TestStatus.builder().favoriteCount(24).id(-123).text("this is test").user(user).build();

    var statusWithoutUser =
        TestStatus.builder().favoriteCount(24).id(123).text("this is test").user(null).build();

    var statusWithInvalidUser =
        TestStatus.builder()
            .favoriteCount(24)
            .id(123)
            .text("this is test")
            .user(userWithoutScreenName)
            .build();

    Flux<Status> statusses =
        Flux.just(
            statusWithRetweetedStatus(statusWithInvalidFavoriteCount),
            statusWithRetweetedStatus(statusWithInvalidId),
            statusWithRetweetedStatus(statusWithoutUser),
            statusWithRetweetedStatus(statusWithInvalidUser));

    // when
    favoriteService.processStatusses(statusses);

    // then
    verify(favoriteRepositoryMock, never()).save(any(FavoriteDoc.class));
  }

  @Test
  public void shouldRemoveOldTwitts() {
    // given

    var expectedResult = Mono.just(22l);

    when(favoriteRepositoryMock.deleteOlderThan(anyLong(), any(TimeUnit.class)))
        .thenReturn(expectedResult);

    // when
    favoriteService.removeOlderThanSec(5);

    // then
    verify(favoriteRepositoryMock, times(1)).deleteOlderThan(anyLong(), any(TimeUnit.class));
  }

  @Test
  public void shouldFetchFavorities() throws InterruptedException {
    // given
    var fdoc1 = FavoriteDoc.builder().id("abc").count(2).username("John").updated(utcNow()).build();
    var fdoc2 = FavoriteDoc.builder().id("abc").count(2).username("John").updated(utcNow()).build();
    var fdoc3 = FavoriteDoc.builder().id("abc").count(2).username("John").updated(utcNow()).build();
    var fdoc4 = FavoriteDoc.builder().id("abc").count(2).username("John").updated(utcNow()).build();

    var f1 =
        Favorite.builder()
            .tweetId(fdoc1.getId())
            .count(fdoc1.getCount())
            .username(fdoc1.getUsername())
            .build();
    var f2 =
        Favorite.builder()
            .tweetId(fdoc1.getId())
            .count(fdoc1.getCount())
            .username(fdoc1.getUsername())
            .build();
    var f3 =
        Favorite.builder()
            .tweetId(fdoc1.getId())
            .count(fdoc1.getCount())
            .username(fdoc1.getUsername())
            .build();
    var f4 =
        Favorite.builder()
            .tweetId(fdoc1.getId())
            .count(fdoc1.getCount())
            .username(fdoc1.getUsername())
            .build();

    var l1 = Lists.list(fdoc1, fdoc2);
    var l2 = Lists.list(fdoc3, fdoc4);

    var docs = Flux.just(l1, l2);

    Flux.just(l1, l2);

    when(favoriteRepositoryMock.top(anyInt())).thenReturn(docs);
    when(favoriteMapperMock.docToDto(any(FavoriteDoc.class))).thenReturn(f1, f2, f3, f4);

    // when
    var result = favoriteService.fetch(2, 1);

    Thread.sleep(3000);

    StepVerifier.create(result.log())
        .assertNext(l -> Assertions.assertEquals(l1.size(), l.size()))
        .assertNext(l -> Assertions.assertEquals(l2.size(), l.size()))
        .expectComplete();
  }

  private Status statusWithRetweetedStatus(Status retweeted) {
    var validStatusUser = TestUser.builder().screenName("John").build();
    return TestStatus.builder()
        .favoriteCount(24)
        .id(123)
        .text("this is test")
        .user(validStatusUser)
        .retweetedStatus(retweeted)
        .build();
  }
}
