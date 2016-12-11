package ab.java.trends.domain.rethink.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.rethinkdb.RethinkDB;
import com.rethinkdb.net.Connection;

import ab.java.trends.config.RethinkDBConfig;
import ab.java.trends.domain.twitter.subscriber.HashTagSubscriber;
import java.util.Arrays;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import twitter4j.HashtagEntity;

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

    public void updateHashtags(HashtagEntity[] hashtags) {
        Arrays.stream(hashtags).forEach(hashtag -> LOGGER.debug("Tag used {}", hashtag.getText()));
    }

}
