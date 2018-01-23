package ab.java.twittertrends.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;

import ab.java.twittertrends.domain.twitter.common.TwitterAuth;



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
		TwitterAuth auth = TwitterAuth.builder()
				.token(env.getRequiredProperty(TOKEN))
				.secret(env.getRequiredProperty(SECRET))
				.customerKey(env.getRequiredProperty(CUSTOMER_KEY))
				.customerSecret(env.getRequiredProperty(CUSTOMER_SECRET))
				.build();
		
		LOGGER.info("Twitter configuration: {}", auth);
		
		return auth;
	}

}
