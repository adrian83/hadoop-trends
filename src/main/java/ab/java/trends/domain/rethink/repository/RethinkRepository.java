package ab.java.trends.domain.rethink.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.rethinkdb.RethinkDB;
import com.rethinkdb.net.Connection;

import ab.java.trends.config.RethinkDBConfig;
import java.util.stream.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class RethinkRepository {

    private static final Logger LOGGER = LoggerFactory.getLogger(RethinkRepository.class);

    @Autowired
    private Connection connection;

    public void saveTwitt(Long id, String text) {

        RethinkDB.r
                .db(RethinkDBConfig.DB_NAME)
                .table(RethinkDBConfig.TABLE_NAME)
                .insert(RethinkDB.r.hashMap("t_id", id).with("text", text))
                .run(connection);

    }

    public void updateHashtag(String hashtag) {
        RethinkDB.r.table("posts").get(1).update(
                post -> RethinkDB.r.hashMap("tag", post.g("views").add(1).default_(0))
        ).run(connection);
    }

    public void updateHashtags(Stream<String> hashtags) {
        hashtags.forEach(hashtag -> LOGGER.warn("Tag used {}", hashtag));
    }

}
