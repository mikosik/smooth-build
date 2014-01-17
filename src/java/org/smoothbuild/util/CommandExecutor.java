package org.smoothbuild.util;

import java.io.IOException;
import java.util.List;

public class CommandExecutor {

  public static int execute(List<String> command) throws InterruptedException, IOException {
    ProcessBuilder processBuilder = new ProcessBuilder(command);

    // TODO collect stdout and stderr and save in cache once
    // MessageType.STDERR/STDOUT is created
    processBuilder.inheritIO();
    Process process = processBuilder.start();
    return process.waitFor();
  }
}
