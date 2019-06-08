package com.github.daggerok;

import io.rsocket.AbstractRSocket;
import io.rsocket.Payload;
import io.rsocket.RSocketFactory;
import io.rsocket.transport.netty.client.TcpClientTransport;
import io.rsocket.transport.netty.server.TcpServerTransport;
import io.rsocket.util.DefaultPayload;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.time.Instant;
import java.util.stream.Stream;

import static java.lang.String.format;

// Junit 5 (Jupiter):

@Log4j2
@ExtendWith(SpringExtension.class)
@DisplayName("RSocket request stream test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
class RSocketRequestResponseTest {

  private static final int port = 7772;
  private static final int expectedCount = 3;

  @BeforeEach
  void producer() {
    RSocketFactory.receive()
                  .resumeCleanupOnKeepAlive()
                  // do not forget about error handling!
                  .errorConsumer(e -> log.error("receiver oops: {}", e.getLocalizedMessage()))
                  .acceptor((payload, receiverSocket) -> {
                    final String name = payload.getDataUtf8();
                    final Stream<String> stream = Stream.generate(() -> format("Hello, %s at %s!",
                                                                               name, Instant.now()));
                    return Mono.just(new AbstractRSocket() {
                      @Override
                      public Flux<Payload> requestStream(Payload payload) {
                        return Flux.fromStream(stream)
                                   .delayElements(Duration.ofMillis(1234))
                                   .map(String::toString)
                                   .map(DefaultPayload::create)
                                   .take(expectedCount)/* // we should not forget cleanup resources, never!
                                   .doFinally(signalType -> receiverSocket.dispose())*/;
                      }
                    });
                  })
                  .transport(TcpServerTransport.create(port))
                  .start()
                  .subscribe();
  }

  @Test
  void test() {
    StepVerifier.create(
        RSocketFactory.connect()
                      //// do not forget about error handling!
                      //.errorConsumer(e -> log.error("requester oops: {}", e.getLocalizedMessage()))
                      .transport(TcpClientTransport.create(port))
                      .start()
                      .flatMapMany(senderSocket -> senderSocket.requestStream(DefaultPayload.create("ololo-trololo!"))
                                                               .map(Payload::getDataUtf8)
                                                               .doOnNext(log::info)/* // do not forget cleanup / close everything!
                                                               .doOnComplete(senderSocket::dispose)*/
                      ))
                .expectNextCount(expectedCount)
                .verifyComplete();
  }
}
