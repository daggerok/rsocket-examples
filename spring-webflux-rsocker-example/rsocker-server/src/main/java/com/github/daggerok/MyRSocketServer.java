package com.github.daggerok;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.web.bind.annotation.RestController;

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

@RestController
class MyResource {

  @MessageMapping("hello")
  public MyResponse hello(MyRequest request) {
    return new MyResponse(request.getPayload());
  }
}

@SpringBootApplication
public class MyRSocketServer {

  public static void main(String[] args) {
    SpringApplication.run(MyRSocketServer.class, args);
  }
}
