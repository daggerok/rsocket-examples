package com.github.daggerok;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class AppTest {

  @Test
  public void main() {
    final App app = new App();
    assertThat(app).isNotNull();
    App.main(new String[0]);
  }
}
