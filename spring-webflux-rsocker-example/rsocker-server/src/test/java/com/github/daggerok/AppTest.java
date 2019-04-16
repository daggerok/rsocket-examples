package com.github.daggerok;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@DisplayName("A Test")
class AppTest {

  @Test
  void main() {
    ConfigurableApplicationContext ctx = new AnnotationConfigApplicationContext(App.class);
    assertThat(ctx).isNotNull();
  }
}
