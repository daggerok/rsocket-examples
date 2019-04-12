package com.github.daggerok;

import com.github.daggerok.client.Heroku;
import lombok.extern.slf4j.Slf4j;

import javax.enterprise.inject.se.SeContainerInitializer;
import javax.enterprise.inject.spi.BeanManager;

@Slf4j
public class App {
  public static void main(String[] args) {
    final SeContainerInitializer initializer =
        SeContainerInitializer.newInstance()
                              .setClassLoader(App.class.getClassLoader())
                              .addPackages(true, BeanManager.class);

    initializer.addPackages(true, Heroku.class)
               .initialize();
  }
}
