package com.github.adrian83.trends.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.mongo.embedded.EmbeddedMongoAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.support.PersistenceExceptionTranslator;
import org.springframework.data.mongodb.ReactiveMongoDatabaseFactory;
import org.springframework.data.mongodb.config.AbstractReactiveMongoConfiguration;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.mapping.event.LoggingEventListener;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;

import com.mongodb.reactivestreams.client.MongoClient;
import com.mongodb.reactivestreams.client.MongoClients;
import com.mongodb.reactivestreams.client.MongoDatabase;

@Configuration
@AutoConfigureAfter(EmbeddedMongoAutoConfiguration.class)
@EnableReactiveMongoRepositories
public class MongoDBConfig extends AbstractReactiveMongoConfiguration {

  private static final String MONGO_URL_PATTERN = "mongodb://%s:%s";

  @Value("${mongo.host}")
  private String host;

  @Value("${mongo.port}")
  private int port;

  @Value("${mongo.dbName}")
  private String databaseName;

  @Bean
  public MongoClient mongoClient() {
    final String connectionStr = String.format(MONGO_URL_PATTERN, host, port);
    return MongoClients.create(connectionStr);
  }

  @Bean
  public ReactiveMongoTemplate mongoTemplate() {
    return new ReactiveMongoTemplate(mongoClient(), getDatabaseName());
  }

  @Bean
  public LoggingEventListener mongoEventListener() {
    return new LoggingEventListener();
  }

  @Bean
  public ReactiveMongoDatabaseFactory factory() {
    return new ReactiveMongoDatabaseFactory() {

      MongoClient client = mongoClient();

      public MongoDatabase getMongoDatabase() throws DataAccessException {
        return client.getDatabase(getDatabaseName());
      }

      public MongoDatabase getMongoDatabase(String dbName) throws DataAccessException {
        return client.getDatabase(dbName);
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
    return databaseName;
  }

  public MongoClient reactiveMongoClient() {
    return mongoClient();
  }
}
