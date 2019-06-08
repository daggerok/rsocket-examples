import org.gradle.api.tasks.testing.logging.TestLogEvent.*

plugins {
  java
  id("io.franzbecker.gradle-lombok") version "3.0.0"
  id("org.springframework.boot") version "2.2.0.BUILD-SNAPSHOT" apply false
  id("io.spring.dependency-management") version "1.0.7.RELEASE" apply false
}

tasks.withType(Wrapper::class.java) {
  val gradleWrapperVersion: String by project
  gradleVersion = gradleWrapperVersion
  distributionType = Wrapper.DistributionType.BIN
}

allprojects {
  apply(plugin = "io.franzbecker.gradle-lombok")
  lombok {
    val lombokVersion: String by project
    version = lombokVersion
  }

  repositories {
    mavenCentral()
    maven { url = uri("https://repo.spring.io/milestone/") }
    maven { url = uri("https://repo.spring.io/snapshot/") }
  }

  defaultTasks("clean", "build")
}

subprojects {
  apply(plugin = "java")

  java {
    val javaVersion = JavaVersion.VERSION_1_8
    sourceCompatibility = javaVersion
    targetCompatibility = javaVersion
  }

  val vavrVersion: String by project
  val junitJupiterVersion: String by project
  dependencies {
    //implementation("org.springframework.boot:spring-boot-starter-rsocket")
    //implementation("org.springframework.boot:spring-boot-starter-webflux")
    testImplementation("org.springframework.boot:spring-boot-starter-test")

    implementation("io.vavr:vavr:$vavrVersion")
    testCompileOnly("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")

    testImplementation(platform("org.junit:junit-bom:$junitJupiterVersion"))
    testRuntime("org.junit.platform:junit-platform-launcher")
    testImplementation("org.junit.jupiter:junit-jupiter-api")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
    testRuntimeOnly("org.junit.vintage:junit-vintage-engine")
    testImplementation("junit:junit")
  }

  tasks.withType<Test> {
    useJUnitPlatform()
    testLogging {
      showExceptions = true
      showStandardStreams = true
      events(PASSED, SKIPPED, FAILED)
    }
  }
}

tasks {
  named("clean") {
    doLast {
      delete(
          project.buildDir,
          "${project.projectDir}/out"
      )
    }
  }
}
