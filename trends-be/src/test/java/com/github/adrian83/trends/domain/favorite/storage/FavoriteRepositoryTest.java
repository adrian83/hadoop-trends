package com.github.adrian83.trends.domain.favorite.storage;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.bson.BsonString;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import com.github.adrian83.trends.domain.favorite.model.FavoriteDoc;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@ExtendWith(MockitoExtension.class)
public class FavoriteRepositoryTest {

  @InjectMocks private FavoriteRepository favoriteRepository;

  @Mock private ReactiveMongoTemplate reactiveMongoTemplateMock;

  @Test
  public void shouldSaveFavorite() {

    // given
    Long id = 143241l;
    FavoriteDoc document =
        FavoriteDoc.builder()
            .id(id.toString())
            .username("John")
            .count(22)
            .updated(12341234123l)
            .build();

    UpdateResult updateResult = UpdateResult.acknowledged(1l, 1l, new BsonString(id.toString()));

    when(reactiveMongoTemplateMock.upsert(any(Query.class), any(Update.class), anyString()))
        .thenReturn(Mono.just(updateResult));

    // when
    Mono<String> monoResult = favoriteRepository.save(document);

    // then
    String docId = monoResult.block();

    assertThat(id.toString(), equalTo(docId));
  }

  @Test
  public void shouldDeleteFavorites() {

    // given
    Long deletedCount = 22l;
    DeleteResult deleteResult = DeleteResult.acknowledged(deletedCount);

    when(reactiveMongoTemplateMock.remove(any(Query.class), anyString()))
        .thenReturn(Mono.just(deleteResult));

    // when
    Mono<Long> monoResult = favoriteRepository.deleteOlderThan(4, TimeUnit.HOURS);

    // then
    Long result = monoResult.block();

    assertThat(result, equalTo(deletedCount));
  }

  @Test
  public void shouldGetMostFavoritedTweets() {

    // given
    FavoriteDoc doc1 =
        FavoriteDoc.builder()
            .id("1343241")
            .username("John")
            .count(112)
            .updated(12341234123l)
            .build();

    FavoriteDoc doc2 =
        FavoriteDoc.builder()
            .id("345334")
            .username("Steve")
            .count(222)
            .updated(12345234123l)
            .build();

    FavoriteDoc doc3 =
        FavoriteDoc.builder()
            .id("3455334")
            .username("Eric")
            .count(121)
            .updated(12345934123l)
            .build();

    FavoriteDoc doc4 =
        FavoriteDoc.builder()
            .id("3415334")
            .username("Sara")
            .count(128)
            .updated(12345914123l)
            .build();

    Flux<FavoriteDoc> docsFlux = Flux.just(doc1, doc2, doc3, doc4);

    when(reactiveMongoTemplateMock.findAll(ArgumentMatchers.<Class<FavoriteDoc>>any(), anyString()))
        .thenReturn(docsFlux);

    // when
    Flux<List<FavoriteDoc>> fluxResult = favoriteRepository.top(3);

    // then
    List<FavoriteDoc> result = fluxResult.blockFirst();

    assertThat(result, hasSize(3));
    assertFavoriteDoc(doc2, result.get(0));
    assertFavoriteDoc(doc4, result.get(1));
    assertFavoriteDoc(doc3, result.get(2));
  }

  private void assertFavoriteDoc(FavoriteDoc expected, FavoriteDoc actual) {
    assertThat(expected.getCount(), equalTo(actual.getCount()));
    assertThat(expected.getId(), equalTo(actual.getId()));
    assertThat(expected.getId(), equalTo(actual.getId()));
    assertThat(expected.getUpdated(), equalTo(actual.getUpdated()));
    assertThat(expected.getUsername(), equalTo(actual.getUsername()));
  }
}
