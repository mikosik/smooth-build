package org.smoothbuild;

import static org.smoothbuild.SmoothConstants.EXIT_CODE_ERROR;

import org.smoothbuild.cli.CommandFailedException;
import org.smoothbuild.cli.Commands;

public class Main {
  public static void main(String[] args) {
    try {
      System.exit(Commands.execute(args));
    } catch (CommandFailedException e) {
      System.exit(EXIT_CODE_ERROR);
    }
  }
}
