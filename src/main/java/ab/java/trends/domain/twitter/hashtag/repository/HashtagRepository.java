package ab.java.trends.domain.twitter.hashtag.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.rethinkdb.RethinkDB;
import com.rethinkdb.net.Connection;

import ab.java.trends.domain.twitter.hashtag.domain.Hashtag;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class HashtagRepository {

    private static final Logger LOGGER = LoggerFactory.getLogger(HashtagRepository.class);

    @Autowired
    private Connection connection;


    public void updateHashtag(String hashtag) {
    	    	
        RethinkDB.r
        .db("twitter_trends")
        .table("hashtags")
        .insert(RethinkDB.r.hashMap("name", hashtag).with("count", 1))
        .optArg("conflict", 
        		(id, old_doc, new_doc) -> new_doc.merge(RethinkDB.r.hashMap("count", old_doc.g("count").add(1))))
        .run(connection);
        
        LOGGER.info("Hashtag {} updated", hashtag);
    }

    public void updateHashtags(Stream<String> hashtags) {
    	hashtags.forEach(hashtag -> updateHashtag(hashtag));
    }
    
    public Stream<Hashtag> findMostPopular(int amount) {
    	
    	ArrayList<Object> result = RethinkDB.r
        .db("twitter_trends")
        .table("hashtags")
        .orderBy(RethinkDB.r.desc("count"))
        .limit(amount).run(connection);
    	
    	List<Hashtag> r = new ArrayList<>();
    	
    	for(Object m : result){
    		@SuppressWarnings("unchecked")
			Map<String, Object> map = (Map<String, Object>)m;
    		r.add( new Hashtag(map.get("name").toString(), Integer.parseInt(map.get("count").toString())));
    	}
    	
    	return r.stream();
    	
    }

}
