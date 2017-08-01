package ab.java.twittertrends.web.twitter.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class ViewController {

	@RequestMapping(value = "/view/hashtags")
	public String index() {
		return "hashtags";
	}
	
}
