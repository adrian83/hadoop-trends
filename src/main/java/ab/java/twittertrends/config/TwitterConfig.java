package ab.java.twittertrends.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;

import ab.java.twittertrends.domain.twitter.domain.TwitterAuth;

@Configuration
@PropertySource("classpath:config/twitter.properties")
public class TwitterConfig {
	
	private static final String CUSTOMER_KEY = "consumer.key";
	private static final String CUSTOMER_SECRET = "consumer.secret";
	private static final String TOKEN = "token";
	private static final String SECRET = "secret";
	
    @Autowired
    private Environment env;
	

	@Bean
	public TwitterAuth getAuhentication() {
		TwitterAuth auth = new TwitterAuth(
				env.getRequiredProperty(TOKEN),
				env.getRequiredProperty(SECRET),
				env.getRequiredProperty(CUSTOMER_KEY),
				env.getRequiredProperty(CUSTOMER_SECRET));
		
		return auth;
	}

	
	
	
}
