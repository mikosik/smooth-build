package org.smoothbuild;

import org.smoothbuild.cli.Cli;

import com.google.inject.Guice;

public class Main {
  public static void main(String[] args) {
    Cli cli = Guice.createInjector(new MainModule()).getInstance(Cli.class);
    int exitCode = cli.run(args);
    System.exit(exitCode);
  }
}
