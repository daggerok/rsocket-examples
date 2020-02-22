plugins {
  idea
  java
  kotlin("jvm")
  kotlin("plugin.spring")
  id("io.franzbecker.gradle-lombok")
  id("com.github.ben-manes.versions")
}

val lombokVersion: String by project
val junit4Version: String by project
val assertkVersion: String by project
val assertjVersion: String by project
val javaVersion = JavaVersion.VERSION_1_8
val junitJupiterVersion: String by project
val gradleWrapperVersion: String by project

allprojects {
  apply<JavaPlugin>()
  apply<io.franzbecker.gradle.lombok.LombokPlugin>()
  apply<org.jetbrains.kotlin.gradle.plugin.KotlinPlatformJvmPlugin>()

  repositories {
    mavenCentral()
  }

  java {
    sourceCompatibility = javaVersion
    targetCompatibility = javaVersion
  }

  lombok {
    version = lombokVersion
  }

  dependencies {
    implementation(kotlin("stdlib"))
    implementation(kotlin("reflect"))
    annotationProcessor("org.projectlombok:lombok:$lombokVersion")
    testImplementation("junit:junit:$junit4Version")
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.vintage:junit-vintage-engine")
    testImplementation(platform("org.junit:junit-bom:$junitJupiterVersion"))
    testImplementation("org.assertj:assertj-core:$assertjVersion")
    testImplementation("com.willowtreeapps.assertk:assertk-jvm:$assertkVersion")
  }
}

tasks {
  withType(Wrapper::class.java) {
    gradleVersion = gradleWrapperVersion
    distributionType = Wrapper.DistributionType.BIN
  }
  withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    kotlinOptions {
      freeCompilerArgs += "-Xjsr305=strict"
      jvmTarget = "$javaVersion"
    }
  }
  withType<Test> {
    useJUnitPlatform()
    testLogging {
      showExceptions = true
      showStandardStreams = true
      events(
          org.gradle.api.tasks.testing.logging.TestLogEvent.PASSED,
          org.gradle.api.tasks.testing.logging.TestLogEvent.SKIPPED,
          org.gradle.api.tasks.testing.logging.TestLogEvent.FAILED
      )
    }
  }
}

defaultTasks("clean", "build")
