package org.smoothbuild.cli.base;

import static org.smoothbuild.SmoothConstants.EXIT_CODE_ERROR;
import static org.smoothbuild.install.ProjectPaths.PRJ_MOD_PATH;
import static org.smoothbuild.install.ProjectPaths.SMOOTH_LOCK_PATH;
import static org.smoothbuild.out.report.Console.printErrorToWriter;
import static org.smoothbuild.util.io.LockFile.lockFile;

import java.io.IOException;
import java.nio.channels.Channel;
import java.nio.channels.FileLock;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.Callable;

import org.smoothbuild.vm.bytecode.BytecodeExc;

public abstract class ProjectCommand extends LoggingCommand implements Callable<Integer> {
  @Override
  public Integer call() {
    Path projectDir = Path.of(".");
    if (!Files.exists(projectDir.resolve(PRJ_MOD_PATH.toString()))) {
      printError("Current directory doesn't have " + PRJ_MOD_PATH.q()
          + ". Is it really smooth enabled project?");
      return EXIT_CODE_ERROR;
    }
    Path normalizedProjectDir = projectDir.normalize();
    FileLock fileLock = lockFile(out(), projectDir.resolve(SMOOTH_LOCK_PATH.toString()));
    if (fileLock == null) {
      return EXIT_CODE_ERROR;
    }
    Channel channel = fileLock.acquiredBy();
    try (channel) {
      return execute(normalizedProjectDir);
    } catch (IOException e) {
      printError("Error closing file lock.");
      return EXIT_CODE_ERROR;
    }
  }

  private Integer execute(Path normalizedProjectDir) {
    try {
      return executeCommand(normalizedProjectDir);
    } catch (BytecodeExc e) {
      printError(e.getMessage());
      return EXIT_CODE_ERROR;
    }
  }

  private void printError(String message) {
    printErrorToWriter(out(), message);
  }

  protected abstract Integer executeCommand(Path projectDir);
}