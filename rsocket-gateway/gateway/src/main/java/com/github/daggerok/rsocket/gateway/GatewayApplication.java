package com.github.daggerok.rsocket.gateway;

import lombok.*;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.UUID;

@Data
@NoArgsConstructor
class Message {
    private UUID uuid;
    private String name;
}

@Data
@NoArgsConstructor
class MessageRequest {
    private String message;
}

@Data
@NoArgsConstructor
@AllArgsConstructor(staticName = "of")
class MessageResponse {
    private Message message;
}

@SpringBootApplication
public class GatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(GatewayApplication.class, args);
    }

}
