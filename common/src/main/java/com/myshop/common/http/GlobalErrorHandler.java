package com.myshop.common.http;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebExceptionHandler;
import reactor.core.publisher.Mono;

@Slf4j
@Configuration
public class GlobalErrorHandler implements WebExceptionHandler {

  private final ObjectMapper objectMapper = new ObjectMapper();

  @Override
  public Mono<Void> handle(ServerWebExchange serverWebExchange, Throwable throwable) {

    DataBufferFactory bufferFactory = serverWebExchange.getResponse().bufferFactory();
    if (throwable instanceof ServiceException) {
      try {
        ServiceException exception = (ServiceException) throwable;
        serverWebExchange.getResponse().setStatusCode(exception.getCodeStatus().getStatus());
        DataBuffer dataBuffer = null;
        try {
          dataBuffer = bufferFactory.wrap(objectMapper.writeValueAsBytes(ApiResponse.fromErrorCode(exception.getCodeStatus())));
        } catch (JsonProcessingException e) {
          e.printStackTrace();
        }
        return serverWebExchange.getResponse().writeWith(Mono.just(dataBuffer));
      } catch (Exception e){
        e.printStackTrace();
        return null;
      }
    } else {
      log.error("error", throwable);
      if (throwable.getLocalizedMessage().contains("Jwt expired")){
        serverWebExchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
      }else{
        serverWebExchange.getResponse().setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);
      }
      serverWebExchange.getResponse().getHeaders().setContentType(MediaType.TEXT_PLAIN);
//      DataBuffer dataBuffer = bufferFactory.wrap("Unknown error".getBytes());
      return serverWebExchange.getResponse().setComplete();
    }
  }

}