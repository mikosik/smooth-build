package org.smoothbuild;

import org.smoothbuild.cli.Commands;

public class Main {
  public static void main(String[] args) {
    int exitCode = Commands.execute(args);
    System.exit(exitCode);
  }
}
