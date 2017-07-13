package ab.java.twittertrends.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.support.PersistenceExceptionTranslator;
import org.springframework.data.mongodb.ReactiveMongoDatabaseFactory;
import org.springframework.data.mongodb.config.AbstractReactiveMongoConfiguration;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;

import com.mongodb.reactivestreams.client.MongoClient;
import com.mongodb.reactivestreams.client.MongoClients;
import com.mongodb.reactivestreams.client.MongoDatabase;

@Configuration
@PropertySource("classpath:config/mongodb.properties")
@EnableReactiveMongoRepositories
public class MongoDBConfig extends AbstractReactiveMongoConfiguration {

	private static final String HOST_PROP = "mongo_host";
	private static final String PORT_PROP = "mongo_port";
	private static final String DB_NAME_PROP = "mongo_db_name";

	@Autowired
	private Environment env;

	@Bean
	public MongoClient mongoClient() {
		final String connectionStr = String.format("mongodb://%s:%s", 
				env.getRequiredProperty(HOST_PROP),
				env.getRequiredProperty(PORT_PROP, Integer.class));
		return MongoClients.create(connectionStr);
	}

	@Bean
	public ReactiveMongoDatabaseFactory factory() {
		return new ReactiveMongoDatabaseFactory() {
			public MongoDatabase getMongoDatabase() throws DataAccessException {
				return mongoClient().getDatabase(getDatabaseName());
			}

			public MongoDatabase getMongoDatabase(String dbName) throws DataAccessException {
				return mongoClient().getDatabase(dbName);
			}

			public PersistenceExceptionTranslator getExceptionTranslator() {
				return new PersistenceExceptionTranslator() {
					public DataAccessException translateExceptionIfPossible(RuntimeException ex) {
						return new DataAccessException("DataAccessException", ex) {
							static final long serialVersionUID = 231231L;
						};
					}
				};
			}
		};
	}

	public String getDatabaseName() {
		return env.getRequiredProperty(DB_NAME_PROP);
	}

}
