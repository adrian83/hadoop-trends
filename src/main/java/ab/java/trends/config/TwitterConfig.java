package ab.java.trends.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;

import com.twitter.hbc.httpclient.auth.Authentication;
import com.twitter.hbc.httpclient.auth.OAuth1;

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
	public Authentication getAuhentication() {
		Authentication auth = new OAuth1(
				env.getRequiredProperty(CUSTOMER_KEY),
				env.getRequiredProperty(CUSTOMER_SECRET),
				env.getRequiredProperty(TOKEN),
				env.getRequiredProperty(SECRET));
		return auth;
	}

	
	
	
}
