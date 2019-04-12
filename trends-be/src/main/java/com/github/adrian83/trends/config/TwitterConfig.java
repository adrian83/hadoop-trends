package com.github.adrian83.trends.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;

@Configuration
@PropertySource("classpath:config/twitter.properties")
public class TwitterConfig {

	private static final Logger LOGGER = LoggerFactory.getLogger(TwitterConfig.class);

	private static final String CUSTOMER_KEY = "consumer.key";
	private static final String CUSTOMER_SECRET = "consumer.secret";
	private static final String TOKEN = "token";
	private static final String SECRET = "secret";

	@Autowired
	private Environment env;

	@Bean
	public TwitterAuth getAuhentication() {
		var auth = new TwitterAuth(env.getRequiredProperty(TOKEN), env.getRequiredProperty(SECRET),
				env.getRequiredProperty(CUSTOMER_KEY), env.getRequiredProperty(CUSTOMER_SECRET));

		LOGGER.info("Twitter configuration: {}", auth);

		return auth;
	}

}
