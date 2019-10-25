package com.github.adrian83.trends.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.mongo.embedded.EmbeddedMongoAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractReactiveMongoConfiguration;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;

import com.mongodb.reactivestreams.client.MongoClient;
import com.mongodb.reactivestreams.client.MongoClients;

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
  @Override
  public MongoClient reactiveMongoClient() {
    final String connectionStr = String.format(MONGO_URL_PATTERN, host, port);   
    return MongoClients.create(connectionStr);
  }

  @Override
  protected String getDatabaseName() {
    return databaseName;
  }
  
    @Bean
    public ReactiveMongoTemplate mongoTemplate(MongoClient client) {
      return new ReactiveMongoTemplate(client, getDatabaseName());
    }
}
