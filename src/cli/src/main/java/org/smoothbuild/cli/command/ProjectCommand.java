package org.smoothbuild.cli.command;

import static org.smoothbuild.cli.Main.EXIT_CODE_ERROR;
import static org.smoothbuild.common.io.LockFile.lockFile;

import java.io.IOException;
import java.nio.channels.Channel;
import java.nio.channels.FileLock;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.Callable;
import org.smoothbuild.cli.layout.Layout;

public abstract class ProjectCommand extends LoggingCommand implements Callable<Integer> {
  @Override
  public Integer call() {
    Path projectDir = Path.of(".");
    if (!Files.exists(projectDir.resolve(Layout.DEFAULT_MODULE_PATH.toString()))) {
      printError("Current directory doesn't have " + Layout.DEFAULT_MODULE_PATH.q()
          + ". Is it really smooth enabled project?");
      return EXIT_CODE_ERROR;
    }
    Path normalizedProjectDir = projectDir.normalize();
    FileLock fileLock = lockFile(out(), projectDir.resolve(Layout.SMOOTH_LOCK_PATH.toString()));
    if (fileLock == null) {
      return EXIT_CODE_ERROR;
    }
    Channel channel = fileLock.acquiredBy();
    try (channel) {
      return executeCommand(normalizedProjectDir);
    } catch (IOException e) {
      printError("Error closing file lock.");
      return EXIT_CODE_ERROR;
    }
  }

  private void printError(String message) {
    out().println("smooth: error: " + message);
  }

  protected abstract Integer executeCommand(Path projectDir);
}
