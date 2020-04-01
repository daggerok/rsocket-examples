package daggerok;

import io.rsocket.AbstractRSocket;
import io.rsocket.Payload;
import io.rsocket.RSocket;
import io.rsocket.RSocketFactory;
import io.rsocket.transport.netty.client.TcpClientTransport;
import io.rsocket.transport.netty.server.TcpServerTransport;
import io.rsocket.util.DefaultPayload;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
class MyRequestStream extends AbstractRSocket {

  @Override
  public Flux<Payload> requestStream(Payload payload) {
    return Mono.just(payload)
               .map(Payload::getDataUtf8)
               .doOnNext(req -> log.info("handling: {}", req))
               .flatMapMany(aString -> Flux.interval(Duration.ofSeconds(1))
                                           .map(aLong -> String.format("%s-%d", aString, aLong))
                                           .take(aString.length()))
               .doOnNext(signal -> log.info("handled: {}", signal))
               .map(DefaultPayload::create);
  }
}

@Slf4j
class MyServer {
  final Disposable listener;

  MyServer() {
    listener = RSocketFactory.receive()
                             .acceptor((setup, sendingSocket) -> Mono.just(new MyRequestStream()))
                             .transport(TcpServerTransport.create(7000))
                             .start()
                             .subscribe();
  }
}

class MyClient {
  final Mono<RSocket> requester;

  MyClient() {
    requester = RSocketFactory.connect()
                              .keepAliveAckTimeout(Duration.ofSeconds(3))
                              .transport(TcpClientTransport.create(7000))
                              .start();
  }
}

@Slf4j
class SimpleTest {

  @Test
  void test() {
    MyServer server = new MyServer();
    MyClient client = new MyClient();
    String payload = "Hello";

    // no back pressure:
    StepVerifier.create(client.requester.flatMapMany(rr -> rr.requestStream(DefaultPayload.create(payload)))
                                        .map(Payload::getDataUtf8))
                // .expectNextMatches(s -> s.contains("Hello-"))
                .expectNextCount(5)
                .verifyComplete();

    // back-pressure: request only 2 items...
    StepVerifier.create(client.requester.flatMapMany(rr -> rr.requestStream(DefaultPayload.create(payload))
                                                             .take(2)) // back-pressure
                                        .map(Payload::getDataUtf8))
                .expectNextMatches(s -> s.contains("Hello-0"))
                .expectNextMatches(s -> s.endsWith("Hello-1"))
                // .expectNextCount(2)
                .verifyComplete();

    // client.requester.flatMapMany(rr -> rr.requestStream(DefaultPayload.create(payload))
    //                                      .take(2)) // back-pressure
    //                 .map(Payload::getDataUtf8)
    //                 .map(res -> assertThat(res).containsIgnoringCase("hello-"))
    //                 .subscribe(s -> log.info("client: {}", s));
    //
    // io.vavr.control.Try.run(() -> Thread.sleep(payload.length() * 1234))
    //                    .andFinally(server.listener::dispose);
    server.listener.dispose();
  }
}
