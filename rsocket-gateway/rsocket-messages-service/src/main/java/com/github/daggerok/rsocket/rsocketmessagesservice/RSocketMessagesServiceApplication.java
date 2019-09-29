package com.github.daggerok.rsocket.rsocketmessagesservice;

import io.vavr.API;
import lombok.*;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.data.annotation.Id;
import org.springframework.data.keyvalue.annotation.KeySpace;
import org.springframework.data.keyvalue.repository.KeyValueRepository;
import org.springframework.data.map.repository.config.EnableMapRepositories;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.Repository;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.UUID;
import java.util.stream.Stream;

import static io.vavr.API.$;
import static io.vavr.API.Case;
import static io.vavr.Predicates.instanceOf;

@Data
@NoArgsConstructor
@KeySpace("messages")
@RequiredArgsConstructor(staticName = "of")
class Message {
    @Id
    private UUID uuid;
    @NonNull
    private String name;
}

interface MessagesKeyValueRepository extends KeyValueRepository<Message, UUID> {}

interface MessagesCrudRepository extends CrudRepository<Message, UUID> {}

interface MessagesPagingAndSortingRepository extends PagingAndSortingRepository<Message, UUID> {}

@Configuration
@EnableMapRepositories(basePackageClasses = RSocketMessagesServiceApplication.class)
class MapRepositoriesConfig {}

@Log4j2
@Configuration
@RequiredArgsConstructor
class TestData {

    private final MessagesCrudRepository messagesCrudRepository;
    private final MessagesKeyValueRepository messagesKeyValueRepository;
    private final MessagesPagingAndSortingRepository pagingAndSortingRepository;

    @EventListener
    public void on(ApplicationStartedEvent event) {
        keyValue("kv");
        crud("crud");
        pagingAndSorting("ps");
        // WebClient.create("http://127.0.0.1:8080/actuator/shutdown")
        //          .post()
        //          .exchange()
        //          .flatMap(clientResponse -> clientResponse.bodyToMono(Map.class))
        //          .doOnEach(log::info)
        //          .blockOptional(Duration.ofSeconds(2));
    }

    private void pagingAndSorting(String pagingAndSortingPrefix) {
        accept(pagingAndSortingRepository, pagingAndSortingPrefix, "one", "two", "three");
    }

    private void crud(String crudPrefix) {
        accept(messagesCrudRepository, crudPrefix, "hola", "hello", "hahaha");
    }

    private void keyValue(String keyValuePrefix) {
        accept(messagesKeyValueRepository, keyValuePrefix, "ololo", "trololo", "hohoho");
    }

    private void accept(Repository repository, String prefix, String... inputs) {
        log.info("insert {}...", prefix);
        Stream.of(inputs)
              .map("-"::concat)
              .map(prefix::concat)
              .map(Message::of)
              .forEach(message -> API.Match(repository).of(
                      Case($(instanceOf(MessagesCrudRepository.class)), r -> r.save(message)),
                      Case($(instanceOf(MessagesKeyValueRepository.class)), r -> r.save(message)),
                      Case($(instanceOf(MessagesPagingAndSortingRepository.class)), r -> r.save(message))
              ));
        log.info("read with {}", repository.getClass());
        API.Match(repository).of(
                Case($(instanceOf(MessagesCrudRepository.class)), CrudRepository::findAll),
                Case($(instanceOf(MessagesKeyValueRepository.class)), MessagesKeyValueRepository::findAll),
                Case($(instanceOf(MessagesPagingAndSortingRepository.class)), MessagesPagingAndSortingRepository::findAll))
           .forEach(log::info);
    }
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

@Controller
@RequiredArgsConstructor
class MessagingEndpoint {

    private final MessagesCrudRepository repository;

    @MessageMapping("messages")
    public Flux<MessageResponse> handle(MessageRequest request) {
        return Flux.just(request.getMessage())
                   .map(Message::of)
                   .map(repository::save)
                   .map(MessageResponse::of)
                   .delayElements(Duration.ofSeconds(1));
    }
}

@SpringBootApplication
public class RSocketMessagesServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(RSocketMessagesServiceApplication.class, args)
                         .registerShutdownHook();
    }
}
