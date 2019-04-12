package com.github.daggerok;

import com.github.daggerok.client.Heroku;
import org.jboss.weld.environment.se.beans.ParametersFactory;

import javax.enterprise.inject.se.SeContainerInitializer;
import javax.enterprise.inject.spi.BeanManager;

public class App {
  public static void main(String[] args) {
    // TODO: new ParametersFactory().setArgs(args);
    SeContainerInitializer.newInstance()
                          //.disableDiscovery()
                          .setClassLoader(App.class.getClassLoader())
                          .addPackages(true, BeanManager.class)
                          .addPackages(true, Heroku.class)
                          .initialize();
  }
}
