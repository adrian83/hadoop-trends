package com.github.adrian83.trends.domain.favorite.logic;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.never;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.github.adrian83.trends.domain.common.Repository;
import com.github.adrian83.trends.domain.favorite.model.FavoriteDoc;
import com.github.adrian83.trends.domain.favorite.model.FavoriteMapper;
import com.github.adrian83.trends.domain.status.TestStatus;
import com.github.adrian83.trends.domain.status.TestUser;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
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
