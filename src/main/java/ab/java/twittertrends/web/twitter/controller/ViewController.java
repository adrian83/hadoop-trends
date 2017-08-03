package ab.java.twittertrends.web.twitter.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class ViewController {

    @RequestMapping(value = {"/", "/index"})
    public String index() {
        return "index";
    }
	
	@RequestMapping(value = "/view/hashtags")
	public String hashtags() {
		return "hashtags";
	}
	
	@RequestMapping(value = "/view/retwitts")
	public String retwitts() {
		return "retwitts";
	}
	
	@RequestMapping(value = "/view/favorites")
	public String favorites() {
		return "favorites";
	}
	
}
