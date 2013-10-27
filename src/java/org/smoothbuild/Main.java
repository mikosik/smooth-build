package org.smoothbuild;

import static com.google.inject.Guice.createInjector;

import org.smoothbuild.app.SmoothApp;
import org.smoothbuild.object.ObjectModule;

public class Main {
  public static void main(String[] args) {
    createInjector(new ObjectModule()).getInstance(SmoothApp.class).run(args);
  }
}
