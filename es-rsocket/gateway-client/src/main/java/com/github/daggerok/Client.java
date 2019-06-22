package com.github.daggerok;

import io.rsocket.RSocket;
import io.rsocket.RSocketFactory;
import io.rsocket.frame.decoder.PayloadDecoder;
import io.rsocket.transport.netty.client.TcpClientTransport;
import lombok.*;
import lombok.experimental.Wither;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.messaging.rsocket.RSocketStrategies;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import java.util.Map;
import java.util.UUID;

import static lombok.AccessLevel.PRIVATE;
import static reactor.core.scheduler.Schedulers.elastic;

@Configuration
class RSocketCfg {

  @Bean
  public RSocket rSocket() {
    return RSocketFactory.connect()
        .dataMimeType(MimeTypeUtils.APPLICATION_JSON_VALUE)
        .frameDecoder(PayloadDecoder.ZERO_COPY)
        .transport(TcpClientTransport.create(12345))
        .start()
        .subscribeOn(elastic())
        .block();
  }

  @Bean
  public RSocketRequester rSocketRequester(RSocketStrategies strategies) {
    return RSocketRequester.wrap(rSocket(), MimeTypeUtils.APPLICATION_JSON, strategies);
  }
}

@Value
@Wither
class Command {
  private final UUID aggregateId;
  private final Map data;
}

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
class CommandResource {

  private final RSocketRequester rs;

  @PostMapping("/add-command")
  Flux<Command> addCommand(@RequestBody Command command) {
    UUID aggregateId = command.getAggregateId() == null ?
        UUID.randomUUID() : command.getAggregateId();
    return rs.route("add-command")
        .data(command.withAggregateId(aggregateId))
        .retrieveFlux(Command.class);
  }

  @GetMapping(path = "/stream-commands/{aggregateId}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
  public Flux<Command> streamCommands(@PathVariable UUID aggregateId) {
    return rs
        .route("stream-commands")
        .data(aggregateId)
        .retrieveFlux(Command.class);
  }
}

@SpringBootApplication
public class Client {

  public static void main(String[] args) {
    SpringApplication.run(Client.class, args);
  }
}
