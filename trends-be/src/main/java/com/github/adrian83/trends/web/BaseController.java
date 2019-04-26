package com.github.adrian83.trends.web;

import java.util.List;

import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Controller;

import reactor.core.publisher.Flux;

@Controller
public class BaseController<T> {

  public static final String SSE_CONTENT_TYPE = "text/event-stream";

  public static final String VIEW_PATH = "/view/";
  public static final String SSE_PATH = "/sse/";

  protected Flux<ServerSentEvent<List<T>>> toSse(Flux<List<T>> elements) {
    return elements.map(l -> ServerSentEvent.builder(l).build());
  }
}
