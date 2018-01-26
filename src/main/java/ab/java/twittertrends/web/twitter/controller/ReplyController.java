package ab.java.twittertrends.web.twitter.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import ab.java.twittertrends.domain.twitter.common.Service;
import ab.java.twittertrends.domain.twitter.reply.Reply;
import reactor.core.publisher.Flux;

@RestController
public class ReplyController extends SseController<Reply> {

	@Autowired
	private Service<Reply> replyFetcher;

	@GetMapping(value = SSE_PATH + REPLIES, produces = SSE_CONTENT_TYPE)
	public Flux<ServerSentEvent<List<Reply>>> sseReplies() {
		return toSse(replyFetcher.elements());
	}

}
