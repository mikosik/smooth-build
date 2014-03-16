package org.smoothbuild;

import org.smoothbuild.cli.Cli;

import com.google.inject.Guice;

public class Main {
  public static void main(String[] args) {
    Cli cli = Guice.createInjector(new MainModule()).getInstance(Cli.class);
    boolean success = cli.run(args);
    System.exit(success ? 0 : 1);
  }
}
