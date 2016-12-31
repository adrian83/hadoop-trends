package ab.java.trends.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
//@EnableWebMvc
@ComponentScan("ab.java.trends.web")
public class WebConfig { // extends WebMvcConfigurerAdapter {
 /*
	@Bean
    public ViewResolver getViewResolver(){
		UrlBasedViewResolver resolver = new org.springframework.web.servlet.view.UrlBasedViewResolver();
        resolver.setViewClass(JstlView.class);
        resolver.setPrefix("/WEB-INF/view/jsp/");
        resolver.setSuffix(".jsp");
        return resolver;
    }
	*/
}