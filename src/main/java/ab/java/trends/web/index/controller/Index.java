package ab.java.trends.web.index.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import ab.java.trends.domain.twitter.service.TwitterService;
import rx.Subscriber;
import twitter4j.Status;

@Controller
public class Index {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(Index.class);

	@Autowired
	private TwitterService twitterService;
	
    @ResponseBody
    @RequestMapping(value = "/")
    public String helloWorld() {
    	LOGGER.debug("------- just a test --------");
    	

    	
        return "Hello World";
    }
    
    @RequestMapping(value = {"trends/index","/index"})
    public String index() {
    	
        return "index";
    }
    
}