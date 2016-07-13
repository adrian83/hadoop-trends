package ab.java.hadoop.trends.domain.twitter.service.impl;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.twitter.hbc.ClientBuilder;
import com.twitter.hbc.core.Constants;
import com.twitter.hbc.core.endpoint.StatusesSampleEndpoint;
import com.twitter.hbc.core.processor.StringDelimitedProcessor;
import com.twitter.hbc.httpclient.BasicClient;
import com.twitter.hbc.httpclient.auth.Authentication;

import ab.java.hadoop.trends.config.TwitterConfig;
import ab.java.hadoop.trends.domain.twitter.service.TwitterService;
import ab.java.hadoop.trends.web.index.controller.Index;
import rx.Observable;
import rx.Observer;

@Service
public class TwitterServiceImpl implements TwitterService {

	private static final Logger LOGGER = LoggerFactory.getLogger(TwitterServiceImpl.class);
	
	@Autowired
	private TwitterConfig config;

	@Override
	public void getTwitts() {
		BlockingQueue<String> queue = new LinkedBlockingQueue<String>(10000);

	    // Define our endpoint: By default, delimited=length is set (we need this for our processor)
	    // and stall warnings are on.
	    StatusesSampleEndpoint endpoint = new StatusesSampleEndpoint();
	    endpoint.stallWarnings(false);

	    Authentication auth = config.getAuhentication();
	    //Authentication auth = new com.twitter.hbc.httpclient.auth.BasicAuth(username, password);

	    // Create a new BasicClient. By default gzip is enabled.
	    BasicClient client = new ClientBuilder()
	            .name("client")
	            .hosts(Constants.STREAM_HOST)
	            .endpoint(endpoint)
	            .authentication(auth)
	            .processor(new StringDelimitedProcessor(queue))
	            .build();

	    Observable<String> observable = Observable.from(queue);
	    

	    
	    
	    Observer<String> observe = new Observer<String> (){

			@Override
			public void onCompleted() {
			}

			@Override
			public void onError(Throwable e) {
				LOGGER.error(String.format("Error: %s", e));
			}

			@Override
			public void onNext(String t) {
				LOGGER.debug(t);
			}
	    	
	    };
	    
	    observable.subscribe(observe);
	    
	    // Establish a connection
	    client.connect();
	   /*
	    // Do whatever needs to be done with messages
	    for (int msgRead = 0; msgRead < 1000; msgRead++) {
	      if (client.isDone()) {
	        System.out.println("Client connection closed unexpectedly: " + client.getExitEvent().getMessage());
	        break;
	      }

	      Observable<String>
	      
	      String msg = queue.poll(5, TimeUnit.SECONDS);
	      if (msg == null) {
	        System.out.println("Did not receive a message in 5 seconds");
	      } else {
	        System.out.println(msg);
	      }
	    }
*/
	    
	    try {
			Thread.currentThread().sleep(5000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			LOGGER.debug(e.getMessage());
		}
	    
	    client.stop();

	}
	

	
}
