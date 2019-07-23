./sbtw clean compile ; (./sbtw "runMain com.github.daggerok.scalarsocket.Server" &) ; sleep 3s ; ./sbtw "runMain com.github.daggerok.scalarsocket.Client"
