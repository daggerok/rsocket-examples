# es-rsocket
Spring WebFlux + RSocket

```bash
http :8080/api/v1/add-command data:='{"ololo":"trololo","aggregateId":"83e24ff7-38fb-4d55-9399-9dbe5a926bd1"}' aggregateId=83e24ff7-38fb-4d55-9399-9dbe5a926bd1
http :8080/api/v1/add-command data:='{"trololo":"ololo","aggregateId":"83e24ff7-38fb-4d55-9399-9dbe5a926bd1"}' aggregateId=83e24ff7-38fb-4d55-9399-9dbe5a926bd1
http :8080/api/v1/stream-commands/83e24ff7-38fb-4d55-9399-9dbe5a926bd1
```

NOTE: _This project has been based on [GitHub: daggerok/main-starter](https://github.com/daggerok/main-starter)_
