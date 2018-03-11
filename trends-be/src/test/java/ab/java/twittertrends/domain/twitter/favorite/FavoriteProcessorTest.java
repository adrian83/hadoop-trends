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
		UpdateResult updateResult = mock(UpdateResult.class); 
		Flux<Status> statusFlux = Flux.just(validStatus());
		
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
	public void shouldFilterOutNotFavoriteStatuses() {
		// given
		TestStatus status = invalidStatus();
		//status.setFavoriteCount(0);
		
		Flux<Status> statusFlux = Flux.just(status);
		
		when(twittsSourceMock.twittsFlux()).thenReturn(statusFlux);

		// when
		favoriteProcessor.postCreate();
		
		// then
		verify(twittsSourceMock).twittsFlux();
		verify(favoriteRepositoryMock, never()).save(any(Favorite.class));
	}
	
	private TestStatus validStatus() {
		return TestStatus.builder()
				.id(1321l)
				.retweetedStatus(TestStatus.builder()
						.id(88)
						.favoriteCount(20)
						.user(TestUser.builder()
								.screenName("John007")
								.build())
						.build())
				.build();
	}
	
	private TestStatus invalidStatus() {
		return TestStatus.builder()
				.id(1321l)
				.retweetedStatus(TestStatus.builder()
						.id(88)
						.favoriteCount(0)
						.user(TestUser.builder()
								.screenName("John007")
								.build())
						.build())
				.build();
	}
	
}
