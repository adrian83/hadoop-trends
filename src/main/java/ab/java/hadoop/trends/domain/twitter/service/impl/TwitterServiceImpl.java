package ab.java.hadoop.trends.domain.twitter.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ab.java.hadoop.trends.config.TwitterConfig;
import ab.java.hadoop.trends.domain.twitter.service.TwitterService;

@Service
public class TwitterServiceImpl implements TwitterService {

	@Autowired
	private TwitterConfig config;
	
	
}
