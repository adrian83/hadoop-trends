package ab.java.trends.domain.twitter.hashtag.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.rethinkdb.RethinkDB;
import com.rethinkdb.gen.ast.Table;
import com.rethinkdb.net.Connection;

import ab.java.trends.config.RethinkDBConfig;
import ab.java.trends.domain.twitter.hashtag.domain.Hashtag;
import ab.java.trends.domain.twitter.hashtag.domain.PopularHashtags;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class HashtagRepository {

	private static final Logger LOGGER = LoggerFactory.getLogger(HashtagRepository.class);
	
	private final static String DB_NAME = RethinkDBConfig.DB_NAME;
	private final static String TABLE_NAME = RethinkDBConfig.HASHTAG_TABLE_NAME;
	private final static String NAME_FIELD = RethinkDBConfig.HASHTAG_PK_NAME;
	private final static String COUNT_FIELD = "count";

	@Autowired
	private Connection connection;
	
	private Table hashtags(){
		return RethinkDB.r.db(DB_NAME).table(TABLE_NAME);
	}

	public void updateHashtag(String hashtag) {

		hashtags()
				.insert(RethinkDB.r
						.hashMap(NAME_FIELD, hashtag)
						.with(COUNT_FIELD, 1))
				.optArg("conflict", (id, old_doc, new_doc) -> new_doc.merge(RethinkDB.r.hashMap(COUNT_FIELD, old_doc.g(COUNT_FIELD).add(1))))
				.run(connection);

		LOGGER.info("Hashtag {} updated", hashtag);
	}

	public void updateHashtags(Stream<String> hashtags) {
		hashtags.forEach(hashtag -> updateHashtag(hashtag));
	}

	public CompletionStage<PopularHashtags> findMostPopular(int amount) {

		CompletionStage<PopularHashtags> ph = CompletableFuture
				.supplyAsync(() -> {
					
			ArrayList<Map<String, Object>> result = hashtags()
					.orderBy(RethinkDB.r.desc(COUNT_FIELD))
					.limit(amount)
					.run(connection);

			Stream<Hashtag> str = result
					.stream()
					.map(o -> new Hashtag(o.get(NAME_FIELD).toString(), Integer.parseInt(o.get(COUNT_FIELD).toString())));

			return new PopularHashtags(LocalDate.now(), str.collect(Collectors.toList()));
		});

		return ph;
	}

}
