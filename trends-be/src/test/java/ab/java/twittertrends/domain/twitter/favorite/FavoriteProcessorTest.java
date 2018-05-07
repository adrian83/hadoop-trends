package ab.java.twittertrends.domain.twitter.favorite;

import static org.mockito.Mockito.when;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import org.bson.BsonString;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import com.mongodb.client.result.UpdateResult;

import ab.java.twittertrends.domain.twitter.TestStatus;
import ab.java.twittertrends.domain.twitter.TestUser;
import ab.java.twittertrends.domain.twitter.TwittsSource;
import ab.java.twittertrends.domain.twitter.common.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import twitter4j.Status;



@RunWith(MockitoJUnitRunner.class)
public class FavoriteProcessorTest {

	@InjectMocks
	private FavoriteProcessor favoriteProcessor;
	
	@Mock
	private TwittsSource twittsSourceMock;
	
	@Mock
	private Repository<Favorite> favoriteRepositoryMock;
	
	
	@Test
	public void shouldStartPersistingFavorites() {
		// given		
		Mockito.when(twittsSourceMock.twittsFlux()).thenReturn(Flux.empty());
		
		// when
		favoriteProcessor.postCreate();
		
		// then
		Mockito.verify(twittsSourceMock).twittsFlux();
	}
	
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
	
}
