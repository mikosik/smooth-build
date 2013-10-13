package org.smoothbuild;

import static com.google.inject.Guice.createInjector;

import org.smoothbuild.app.SmoothApp;

public class Main {
  public static void main(String[] args) {
    createInjector().getInstance(SmoothApp.class).run(args);
  }
}
