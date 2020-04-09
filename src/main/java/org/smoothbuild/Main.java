package org.smoothbuild;

import static org.smoothbuild.cli.CommandExecutor.executeCommand;

public class Main {
  public static void main(String[] args) {
    System.exit(executeCommand(args));
  }
}
