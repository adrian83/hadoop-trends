package com.github.adrian83.trends.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class IndexController extends BaseController<Object> {
	
	public static final String INDEX = "index";
	
	@RequestMapping(value = { "/", "/" + INDEX })
	public String index() {
		return INDEX;
	}
}
