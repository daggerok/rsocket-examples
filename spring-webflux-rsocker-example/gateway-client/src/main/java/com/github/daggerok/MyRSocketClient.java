package com.github.daggerok;

import io.rsocket.RSocket;
import io.rsocket.RSocketFactory;
import io.rsocket.frame.decoder.PayloadDecoder;
import io.rsocket.transport.netty.client.TcpClientTransport;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.messaging.rsocket.RSocketStrategies;
import org.springframework.stereotype.Service;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import static org.springframework.web.reactive.function.server.RouterFunctions.route;
import static reactor.core.scheduler.Schedulers.elastic;

@Configuration
class RSocketCfg {

  @Bean
  RSocket rSocket() {
    return RSocketFactory.connect()
                         .dataMimeType(MimeTypeUtils.APPLICATION_JSON_VALUE)
                         .frameDecoder(PayloadDecoder.ZERO_COPY)
                         .transport(TcpClientTransport.create(1234))
                         .start()
                         .subscribeOn(elastic())
                         .block();
  }

  @Bean
  RSocketRequester rSocketRequester(RSocketStrategies strategies) {
    return RSocketRequester.wrap(rSocket(), MimeTypeUtils.APPLICATION_JSON, strategies);
  }
}

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
}

//@RestController
//@RequiredArgsConstructor
//@RequestMapping("/api/v1")
//class MyResource {
//
//  private final RSocketRequester rs;
//
//  @GetMapping("/hello/{name}")
//  Mono<MyResponse> hello(@PathVariable(name = "name", required = false) Optional<String> maybeName) {
//    String name = maybeName.orElse("Buddy");
//    return rs.route("hello")
//             .data(new MyRequest(name))
//             .retrieveMono(MyResponse.class)
//             .subscribeOn(elastic());
//  }
//}

@Service
@RequiredArgsConstructor
class MyHandlers {

  private final RSocketRequester rs;

  public Mono<ServerResponse> hello(ServerRequest request) {
    String name = request.pathVariable("name");
    return ServerResponse.ok()
                         .body(rs.route("hello")
                                 .data(new MyRequest(name))
                                 .retrieveMono(MyResponse.class), MyResponse.class);
  }
}

@Configuration
class MyRSocketRoutes {

  @Bean
  RouterFunction<ServerResponse> routes(MyHandlers myHandlers) {
    return route().GET("/api/v1/hello/{name}", myHandlers::hello)
                  .build();
  }
}

@SpringBootApplication
public class MyRSocketClient {

  public static void main(String[] args) {
    SpringApplication.run(MyRSocketClient.class, args);
  }
}
