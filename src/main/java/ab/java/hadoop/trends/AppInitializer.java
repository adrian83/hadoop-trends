package ab.java.hadoop.trends;

import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;

import ab.java.hadoop.trends.config.AppConfig;
import ab.java.hadoop.trends.config.WebConfig;

public class AppInitializer extends AbstractAnnotationConfigDispatcherServletInitializer {
	
	@Override
	protected Class<?>[] getRootConfigClasses() {
		return new Class[] { AppConfig.class };
	}

	@Override
	protected Class<?>[] getServletConfigClasses() {
		return new Class[] { WebConfig.class };
	}

	@Override
	protected String[] getServletMappings() {
		return new String[] { "/" };
	}
	
}





/*
WebApplicationInitializer {

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
*/