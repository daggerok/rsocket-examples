package com.github.daggerok.client;

import io.rsocket.Payload;
import io.rsocket.RSocket;
import io.rsocket.RSocketFactory;
import io.rsocket.transport.netty.client.WebsocketClientTransport;
import io.rsocket.util.DefaultPayload;
import io.vavr.control.Try;
import lombok.extern.slf4j.Slf4j;
import org.jboss.weld.environment.se.bindings.Parameters;
import org.jboss.weld.environment.se.events.ContainerInitialized;

import javax.enterprise.event.Observes;
import javax.inject.Singleton;
import java.net.URI;
import java.time.Duration;
import java.util.List;
import java.util.Objects;

@Slf4j
@Singleton
public class Heroku {

  // TODO: @Inject @Parameters List<String> params;
  // TODO: @Inject @Parameters String[] paramsArray;

  public void on(@Observes ContainerInitialized event, @Parameters List<String> parameters) {
    // TODO: log.info("bootstrap with parameters {}", parameters);

    URI uri = URI.create("ws://rsocket-demo.herokuapp.com/ws");
    WebsocketClientTransport ws = WebsocketClientTransport.create(uri);
    RSocket client = Objects.requireNonNull(
        RSocketFactory.connect()
                      .keepAliveAckTimeout(Duration.ofSeconds(3))
                      .transport(ws)
                      .start()
                      .block());

    Try.run(() -> client.requestStream(DefaultPayload.create("peace"))
                        .take(10)
                        .map(Payload::getDataUtf8)
                        .doOnNext(log::info)
                        .blockLast())
       .andFinally(client::dispose);
  }
}
