package com.github.daggerok;

import com.github.daggerok.client.Heroku;

import javax.enterprise.inject.se.SeContainerInitializer;
import javax.enterprise.inject.spi.BeanManager;

public class App {
  public static void main(String[] args) {
    SeContainerInitializer.newInstance()
                          .disableDiscovery()
                          .setClassLoader(App.class.getClassLoader())
                          .addPackages(true, BeanManager.class)
                          .addPackages(true, Heroku.class)
                          .initialize();
  }
}
