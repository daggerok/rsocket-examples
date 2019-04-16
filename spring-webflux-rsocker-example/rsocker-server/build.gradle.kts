plugins {
  java
  id("org.springframework.boot")
  id("io.spring.dependency-management")
}

dependencies {
  implementation("org.springframework.boot:spring-boot-starter-rsocket")
  implementation("org.springframework.boot:spring-boot-starter-webflux")
}
