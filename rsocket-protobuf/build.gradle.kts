plugins {
  idea
  java
  kotlin("jvm")
  kotlin("plugin.spring")
  id("io.franzbecker.gradle-lombok")
  id("com.github.ben-manes.versions")
}

val main: String by project
val lombokVersion: String by project
val junit4Version: String by project
val assertkVersion: String by project
val assertjVersion: String by project
val junitJupiterVersion: String by project
val gradleWrapperVersion: String by project
val javaVersion: JavaVersion = java11or8()

tasks {
  withType(Wrapper::class.java) {
    gradleVersion = gradleWrapperVersion
    distributionType = Wrapper.DistributionType.BIN
  }
}

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

  tasks {
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
}

subprojects {
  apply<JavaPlugin>()
  tasks {
    register("fatJar", Jar::class.java) {
      //archiveAppendix.set("all")
      archiveClassifier.set("all")
      duplicatesStrategy = DuplicatesStrategy.EXCLUDE
      manifest {
        attributes("Main-Class" to main)
      }
      from(configurations.runtimeClasspath.get()
          .onEach { println("add from dependencies: ${it.name}") }
          .map { if (it.isDirectory) it else zipTree(it) })
      val sourcesMain = sourceSets.main.get()
      sourcesMain.allSource.forEach { println("add from sources: ${it.name}") }
      from(sourcesMain.output)
    }

    named("clean") {
      doLast {
        delete(
            project.buildDir,
            "${project.projectDir}/out"
        )
      }
    }

    build.get().dependsOn("fatJar")
    // build.get().dependsOn("installDist", "fatJar")
  }
}

defaultTasks("clean", "build")

fun java11or8(): JavaVersion {
  val currentJava = org.gradle.internal.jvm.Jvm.current().javaVersion ?: JavaVersion.VERSION_1_8
  val isJava9orAbove = currentJava.ordinal > JavaVersion.VERSION_1_8.ordinal
  return if (isJava9orAbove) JavaVersion.VERSION_11 else JavaVersion.VERSION_1_8
}
