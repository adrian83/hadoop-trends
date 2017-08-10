package ab.java.twittertrends.web.twitter.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ab.java.twittertrends.domain.twitter.reply.Reply;
import ab.java.twittertrends.domain.twitter.reply.ReplyFetcher;
import rx.Observable;

@RestController
public class ReplyController {

	@Autowired
	private ReplyFetcher replyFetcher;

	@RequestMapping(value = "/replies")
	public Observable<List<Reply>> replies() {
		return replyFetcher.replies().first();
	}

	@GetMapping(value = "/sse/replies", produces = "text/event-stream")
	public Observable<ServerSentEvent<List<Reply>>> sseReplies() {
		return replyFetcher.replies().map(l -> ServerSentEvent.builder(l).build());
	}

}
