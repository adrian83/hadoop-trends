package ab.java.trends.domain.rethink.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.rethinkdb.RethinkDB;
import com.rethinkdb.net.Connection;

import ab.java.trends.config.RethinkDBConfig;

@Component
public class RethinkRepository {

	@Autowired
	private Connection connection;

	public void saveTwitt(Long id, String text) {

		RethinkDB.r
		.db(RethinkDBConfig.DB_NAME)
		.table(RethinkDBConfig.TABLE_NAME)
		.insert(RethinkDB.r.hashMap("t_id", id).with("text", text))
		.run(connection);

	}

}
