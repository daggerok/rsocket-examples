package com.github.daggerok;

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
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

import java.time.Duration;

import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
class RSocketCfg {

    @Bean
    Mono<RSocketRequester> rSocketRequester(RSocketStrategies strategies, RSocketRequester.Builder builder) {
        Scheduler scheduler = Schedulers.elastic();
        return builder.rsocketStrategies(strategies)
                      .dataMimeType(MimeTypeUtils.APPLICATION_JSON)
                      //.metadataMimeType(MimeTypeUtils.APPLICATION_JSON) // FIXME: WARNING: DON'T DO THIS!
                      .connectTcp("127.0.0.1", 1234)
                      .retryBackoff(2, Duration.ofSeconds(2))
                      .subscribeOn(scheduler)
                      .publishOn(scheduler);
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

@Service
@RequiredArgsConstructor
class MyHandlers {

    private final Mono<RSocketRequester> rs;

    public Mono<ServerResponse> hello(ServerRequest request) {
        String name = request.pathVariable("name");
        return ServerResponse.ok()
                             .body(rs.flatMap(rr -> rr.route("hello")
                                                      .data(Mono.just(new MyRequest(name)))
                                                      .retrieveMono(MyResponse.class)),
                                   MyResponse.class);
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
