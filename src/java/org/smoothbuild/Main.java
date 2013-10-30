package org.smoothbuild;

import static com.google.inject.Guice.createInjector;

import org.smoothbuild.app.SmoothApp;
import org.smoothbuild.db.DbModule;

public class Main {
  public static void main(String[] args) {
    createInjector(new DbModule()).getInstance(SmoothApp.class).run(args);
  }
}
