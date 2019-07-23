package com.github.daggerok.scalarsocket

import java.time.{Duration, Instant}

import io.rsocket.frame.decoder.PayloadDecoder
import io.rsocket.transport.netty.server.TcpServerTransport
import io.rsocket.util.ByteBufPayload
import io.rsocket._
import org.slf4j.LoggerFactory
import reactor.core.publisher.Mono

case object PingHandler extends SocketAcceptor {
  private val pong = ByteBufPayload.create(s"Hello, World at ${Instant.now()}!")

  private val rSocket = new AbstractRSocket {
    override def requestResponse(payload: Payload): Mono[Payload] = {
      val dataUtf = payload.getDataUtf8
      val metadataUtf = payload.getMetadataUtf8
      LoggerFactory.getLogger(this.getClass).info("server received: {}", dataUtf)
      payload.release()
      Mono.just(pong.retain())
    }
  }

  override def accept(setup: ConnectionSetupPayload, sendingSocket: RSocket): Mono[RSocket] =
    Mono.just(rSocket)
}

case object Server extends App {
  val timeout = Duration.ofSeconds(15)

  RSocketFactory
    .receive()
    .frameDecoder(PayloadDecoder.ZERO_COPY) // latest
    //.frameDecoder(f => f.retain())
    .acceptor(PingHandler)
    .transport(TcpServerTransport.create(7878))
    .start()
    .block(timeout)
    .onClose()
    //.block(timeout) // latest
    .subscribe()

  LoggerFactory.getLogger(this.getClass).info("server ready!")
  Thread.sleep(timeout.toMillis)
  //System.exit(0)
}
