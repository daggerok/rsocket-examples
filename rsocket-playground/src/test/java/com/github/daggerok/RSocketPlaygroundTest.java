package com.github.daggerok;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.GenericApplicationContext;

import static org.assertj.core.api.Assertions.assertThat;

/*
// Junit 4:
import org.junit.Test;

public class RSocketPlaygroundTest {

  @Test
  public void main() {
    GenericApplicationContext ctx = new AnnotationConfigApplicationContext(App.class);
    assertThat(ctx).isNotNull();

    Function<String, String> greeter = ctx.getBean(Function.class);
    assertThat(greeter.apply("Test")).isNotNull()
                                     .isEqualTo("hello, Test!");
  }
}
*/
// Junit 5 (Jupiter):

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("RSocket playgroung")
class RSocketPlaygroundTest {

  @Test
  void main() {
    GenericApplicationContext ctx = new AnnotationConfigApplicationContext(RSocketPlaygroundApp.class);
    assertThat(ctx).isNotNull();
    assertThat(ctx.getBeanDefinitionCount()).isGreaterThan(0);
  }
}
