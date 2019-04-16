package com.github.daggerok;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.GenericApplicationContext;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@DisplayName("A Test")
class AppTest {

  @Test
  void main() {
    GenericApplicationContext ctx = new AnnotationConfigApplicationContext(App.class);
    assertThat(ctx).isNotNull();
  }
}
