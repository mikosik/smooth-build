package org.smoothbuild;

import static com.google.inject.Guice.createInjector;

import org.smoothbuild.run.SmoothRunner;

public class Main {
  public static void main(String[] args) {
    createInjector().getInstance(SmoothRunner.class).run(args);
  }
}
