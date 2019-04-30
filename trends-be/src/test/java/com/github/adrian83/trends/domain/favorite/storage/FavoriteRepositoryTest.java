package com.github.adrian83.trends.domain.favorite.storage;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.bson.BsonString;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import com.github.adrian83.trends.domain.favorite.model.FavoriteDoc;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RunWith(MockitoJUnitRunner.class)
public class FavoriteRepositoryTest {

  @InjectMocks private FavoriteRepository favoriteRepository;

  @Mock private ReactiveMongoTemplate reactiveMongoTemplateMock;

  @Test
  public void shouldSaveFavorite() {

    // given
    FavoriteDoc document = new FavoriteDoc(143241l, "john", 32l, 242414141l);
    UpdateResult updateResult = UpdateResult.acknowledged(1l, 1l, new BsonString("abc-key"));

    when(reactiveMongoTemplateMock.upsert(any(Query.class), any(Update.class), anyString()))
        .thenReturn(Mono.just(updateResult));

    // when
    Mono<UpdateResult> monoResult = favoriteRepository.save(document);

    // then
    UpdateResult result = monoResult.block();

    assertThat(updateResult.getUpsertedId(), equalTo(result.getUpsertedId()));
    assertThat(updateResult.getMatchedCount(), equalTo(result.getMatchedCount()));
    assertThat(updateResult.getModifiedCount(), equalTo(result.getModifiedCount()));
  }

  @Test
  public void shouldDeleteFavorites() {

    // given
    DeleteResult deleteResult = DeleteResult.acknowledged(44l);

    when(reactiveMongoTemplateMock.remove(any(Query.class), anyString()))
        .thenReturn(Mono.just(deleteResult));

    // when
    Mono<DeleteResult> monoResult = favoriteRepository.deleteOlderThan(4, TimeUnit.HOURS);

    // then
    DeleteResult result = monoResult.block();

    assertThat(deleteResult.getDeletedCount(), equalTo(result.getDeletedCount()));
  }

  @Test
  public void shouldGetMostFavoritedTwitts() {

    // given
    FavoriteDoc doc1 = new FavoriteDoc(143241l, "john", 32l, 242414141l);
    FavoriteDoc doc2 = new FavoriteDoc(353434l, "olivia", 12l, 242414141l);
    FavoriteDoc doc3 = new FavoriteDoc(756756l, "eric", 112l, 242414141l);
    FavoriteDoc doc4 = new FavoriteDoc(134511l, "helena", 1l, 242414141l);

    Flux<FavoriteDoc> docsFlux = Flux.just(doc1, doc2, doc3, doc4);

    when(reactiveMongoTemplateMock.findAll(ArgumentMatchers.<Class<FavoriteDoc>>any(), anyString()))
        .thenReturn(docsFlux);

    // when
    Flux<List<FavoriteDoc>> fluxResult = favoriteRepository.top(3);

    // then
    List<FavoriteDoc> result = fluxResult.blockFirst();

    assertThat(result, hasSize(3));
    assertFavoriteDoc(doc3, result.get(0));
    assertFavoriteDoc(doc1, result.get(1));
    assertFavoriteDoc(doc2, result.get(2));
  }

  private void assertFavoriteDoc(FavoriteDoc expected, FavoriteDoc actual) {
    assertThat(expected.getCount(), equalTo(actual.getCount()));
    assertThat(expected.getId(), equalTo(actual.getId()));
    assertThat(expected.getTwittId(), equalTo(actual.getTwittId()));
    assertThat(expected.getUpdated(), equalTo(actual.getUpdated()));
    assertThat(expected.getUsername(), equalTo(actual.getUsername()));
  }
}
