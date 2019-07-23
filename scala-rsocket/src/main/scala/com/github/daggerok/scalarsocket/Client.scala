package com.github.daggerok.scalarsocket

import java.time.Duration

import io.rsocket._
import io.rsocket.frame.decoder.PayloadDecoder // latest
import io.rsocket.transport.netty.client.TcpClientTransport
import io.rsocket.util.ByteBufPayload
import org.slf4j.LoggerFactory

case object Client extends App {
  val client =
    RSocketFactory
      .connect()
      //.frameDecoder(f => f.retain())
      .frameDecoder(PayloadDecoder.ZERO_COPY) // latest
      .transport(TcpClientTransport.create(7878))
      .start()

  val request = ByteBufPayload.create("Hola!")
  val timeout = Duration.ofSeconds(15)
  val log = LoggerFactory.getLogger(this.getClass)

  client
    .flatMap(rSocket => rSocket
      .requestResponse(request))
    .subscribe((r: Payload) => {
      val payload = r.retain()
      log.info("done with {}", payload.getDataUtf8)
    })
    //.block(timeout)
  log.info("client ready!")
  Thread.sleep(timeout.toMillis)
  //System.exit(0)
}
