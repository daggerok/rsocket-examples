package daggerok;

import io.rsocket.AbstractRSocket;
import io.rsocket.Payload;
import io.rsocket.RSocket;
import io.rsocket.RSocketFactory;
import io.rsocket.transport.netty.client.TcpClientTransport;
import io.rsocket.transport.netty.server.TcpServerTransport;
import io.rsocket.util.DefaultPayload;
import io.vavr.control.Try;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
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

  MyServer(int port) {
    listener = RSocketFactory.receive()
                             .acceptor((setup, sendingSocket) -> Mono.just(new MyRequestStream()))
                             .transport(TcpServerTransport.create(port))
                             .start()
                             .subscribe();
  }
}

class MyClient {
  final Mono<RSocket> requester;

  MyClient(int port) {
    requester = RSocketFactory.connect()
                              .keepAliveAckTimeout(Duration.ofSeconds(3))
                              .transport(TcpClientTransport.create(port))
                              .start();
  }
}

@Slf4j
class SimpleTest {

  @Test
  void test() {
    MyServer server = new MyServer(7001);
    MyClient client = new MyClient(7001);
    String payload = "Привет";

    StepVerifier.create(client.requester.flatMapMany(rr -> rr.requestStream(DefaultPayload.create(payload)))
                                        .map(Payload::getDataUtf8)
                                        .doOnEach(stringSignal -> log.info("client 1: {}", stringSignal.get())))
                .expectNextCount(payload.length())
                .verifyComplete();

    server.listener.dispose();
  }

  @Test
  void test_back_pressure() {
    MyServer server = new MyServer(7002);
    MyClient client = new MyClient(7002);
    String payload = "Hello";

    // back-pressure: request only 2 items...
    StepVerifier.create(client.requester.flatMapMany(rr -> rr.requestStream(DefaultPayload.create(payload))
                                                             .take(2)) // back-pressure
                                        .map(Payload::getDataUtf8)
                                        .doOnEach(stringSignal -> log.info("client 2: {}", stringSignal.get())))
                .expectNextMatches(s -> s.contains("Hello-0"))
                .expectNextMatches(s -> s.endsWith("Hello-1"))
                // .expectNextCount(2)
                .verifyComplete();

    server.listener.dispose();
  }

  @Test
  void yet_another() {
    MyServer server = new MyServer(7003);
    MyClient client = new MyClient(7003);
    String payload = "Hola";

    client.requester.flatMapMany(rr -> rr.requestStream(DefaultPayload.create(payload))
                                         .take(2)) // back-pressure
                    .map(Payload::getDataUtf8)
                    .map(res -> assertThat(res).containsIgnoringCase("hola-"))
                    .subscribe(s -> log.info("client 3: {}", s));

    Try.run(() -> Thread.sleep(payload.length() * 1234))
       .andFinally(server.listener::dispose)
       .onFailure(throwable -> log.info("oops: {}", throwable.getLocalizedMessage()));
  }
}
