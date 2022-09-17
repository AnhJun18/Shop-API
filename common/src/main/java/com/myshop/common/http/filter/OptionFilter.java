package com.myshop.common.http.filter;

import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

public class OptionFilter implements WebFilter {

  @Override
  public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
    if (exchange.getRequest().getMethod() == HttpMethod.OPTIONS) {
      exchange.getResponse().setStatusCode(HttpStatus.NO_CONTENT);
      return exchange.getResponse().setComplete();
    }

    return chain.filter(exchange);
  }
}
