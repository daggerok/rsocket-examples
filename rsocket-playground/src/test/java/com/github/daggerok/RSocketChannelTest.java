package com.github.daggerok;

import io.rsocket.AbstractRSocket;
import io.rsocket.Payload;
import io.rsocket.RSocketFactory;
import io.rsocket.transport.netty.client.TcpClientTransport;
import io.rsocket.transport.netty.server.TcpServerTransport;
import io.rsocket.util.DefaultPayload;
import lombok.extern.log4j.Log4j2;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.time.Instant;
import java.util.stream.Stream;

import static java.lang.String.format;

// Junit 4:

@Log4j2
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class RSocketChannelTest {

  private static final int port = 7777;

  @Before
  public void producer() {
    RSocketFactory.receive()
                  .resumeCleanupOnKeepAlive()
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
                                   .map(DefaultPayload::create);
                      }
                    });
                  })
                  .transport(TcpServerTransport.create(port))
                  .start()
                  .subscribe();
  }

  @Test
  public void test() {
    StepVerifier.create(
        RSocketFactory.connect()
                      .transport(TcpClientTransport.create(port))
                      .start()
                      .flatMapMany(senderSocket -> senderSocket.requestStream(DefaultPayload.create("ololo-trololo!"))
                                                               .take(3)
                                                               .map(Payload::getDataUtf8))
                      .doOnNext(log::info))
                .expectNextCount(3)
                .verifyComplete();

  }
}
