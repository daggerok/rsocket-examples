package com.github.daggerok;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import reactor.core.publisher.Mono;

import static java.lang.String.format;
import static java.time.Instant.now;

@Data
@NoArgsConstructor
@AllArgsConstructor
class MyRequest {
  private String payload;
}

@Data
@NoArgsConstructor
@AllArgsConstructor
class MyResponse {
  private String data;

  public String getData() {
    return format("Hello! %s -- %s", now(), data);
  }
}

@Controller
class MyResource {

  @MessageMapping("hello")
  Mono<MyResponse> hello(@RequestBody MyRequest request) {
    return Mono.just(new MyResponse(request.getPayload()));
  }
}

@SpringBootApplication
public class MyRSocketServer {

  public static void main(String[] args) {
    SpringApplication.run(MyRSocketServer.class, args);
  }
}
