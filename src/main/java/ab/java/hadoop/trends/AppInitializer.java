package ab.java.hadoop.trends;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;

import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

public class AppInitializer implements WebApplicationInitializer {

	    @Override
	    public void onStartup(ServletContext servletContext) throws ServletException {
	 

	        AnnotationConfigWebApplicationContext applicationContext = new AnnotationConfigWebApplicationContext();
	        applicationContext.setConfigLocation("ab.java.hadoop.trends.config");
	 
	        DispatcherServlet dispatcherServlet = new DispatcherServlet(applicationContext);
	        ServletRegistration.Dynamic servlet = servletContext.addServlet("mvc-dispatcher", dispatcherServlet);
	 
	        servlet.addMapping("/");
	        servlet.setAsyncSupported(true);
	        servlet.setLoadOnStartup(1);
	    }
	
}
