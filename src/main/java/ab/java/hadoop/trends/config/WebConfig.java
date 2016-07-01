package ab.java.hadoop.trends.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Configuration
@EnableWebMvc
@ComponentScan("ab.java.hadoop.trends.web")
public class WebConfig extends WebMvcConfigurerAdapter {
 
}