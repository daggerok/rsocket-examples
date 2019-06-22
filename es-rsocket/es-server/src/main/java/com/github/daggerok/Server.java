package com.github.daggerok;

import lombok.*;
import lombok.experimental.Wither;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static lombok.AccessLevel.PRIVATE;

@Value
@Wither
class Command {
  private final UUID aggregateId;
  private final Map data;
}

@Repository
class InMemoryStore {

  private final Map<UUID, List<Command>> store = new ConcurrentHashMap<>();

  public List<Command> add(Command command) {
    UUID aggregateId = command.getAggregateId();
    store.putIfAbsent(aggregateId, new CopyOnWriteArrayList<>());
    List<Command> commands = store.get(aggregateId);
    Stream<Command> eventStream = Stream.concat(commands.stream(), Stream.of(command));
    store.put(aggregateId, eventStream.collect(Collectors.toList()));
    return store.get(aggregateId);
  }

  public List<Command> stream(UUID aggregateId) {
    return store.getOrDefault(aggregateId, new ArrayList<>());
  }
}

@Log4j2
@RestController
@RequiredArgsConstructor
class StoreResource {

  private final InMemoryStore store;

  @MessageMapping("add-command")
  public Flux<Command> addCommand(Command cmd) {
    log.info(cmd.toString());
    return Flux.fromIterable(store.add(cmd));
  }

  @MessageMapping("stream-commands")
  public Flux<Command> streamCommands(UUID aggregateId) {
    log.info(aggregateId.toString());
    return Flux.fromIterable(store.stream(aggregateId));
  }
}

@SpringBootApplication
public class Server {

  public static void main(String[] args) {
    SpringApplication.run(Server.class, args);
  }
}
