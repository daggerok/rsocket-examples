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
import org.reactivestreams.Publisher;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Duration;

import static java.lang.String.format;

// Junit 4:

@Log4j2
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class RSocketChannelTest {

  private static final int port = 7771;
  private static final int expectedCount = 3;

  @Before
  public void pong() {
    RSocketFactory.receive()
                  // do not forget about error handling!
                  .errorConsumer(e -> log.error("backend oops: {}", e.getLocalizedMessage()))
                  .resumeCleanupOnKeepAlive()
                  .acceptor((payload, receiverSocket) -> Mono.just(new AbstractRSocket() {
                    @Override
                    public Flux<Payload> requestChannel(Publisher<Payload> payloads) {
                      return Flux.from(payloads)
                                 .map(Payload::getDataUtf8)
                                 .delayElements(Duration.ofMillis(1234))
                                 .map(s -> format("ping-pong: %s", s))
                                 .map(DefaultPayload::create)
                                 .take(expectedCount)/* // do not forget cleanup / close everything!
                                 .doOnComplete(senderSocket::dispose)*/
                                 //// we should not forget cleanup resources, never!
                                 //.doFinally(signalType -> receiverSocket.dispose())
                          ;
                    }
                  }))
                  .transport(TcpServerTransport.create(port))
                  .start()
                  .subscribe();
  }

  @Test
  public void ping() {
    StepVerifier.create(
        RSocketFactory.connect()
                      //// do not forget about error handling!
                      //.errorConsumer(e -> log.error("client oops: {}", e.getLocalizedMessage()))
                      .transport(TcpClientTransport.create(port))
                      .start()
                      .flatMapMany(senderSocket -> senderSocket
                          .requestChannel(Flux.interval(Duration.ofMillis(333))
                                              .map(tick -> DefaultPayload.create("ololo-trololo!")))
                          .map(Payload::getDataUtf8)
                          .doOnNext(log::info)/* // do not forget cleanup / close everything!
                         .doOnComplete(senderSocket::dispose)*//*
                          .doFinally(signal -> {
                            log.error("{} oops... closing...", signal);
                            senderSocket.dispose();
                          })*/
                      ))
                .expectNextCount(expectedCount)
                .verifyComplete();

  }
}
