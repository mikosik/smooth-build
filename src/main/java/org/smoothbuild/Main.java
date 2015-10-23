package org.smoothbuild;

import org.smoothbuild.cli.Commands;

public class Main {
  public static void main(String[] args) {
    System.exit(Commands.execute(args));
  }
}
