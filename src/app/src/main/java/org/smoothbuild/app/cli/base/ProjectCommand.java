package org.smoothbuild.app.cli.base;

import static org.smoothbuild.app.SmoothConstants.EXIT_CODE_ERROR;
import static org.smoothbuild.app.layout.Layout.DEFAULT_MODULE_PATH;
import static org.smoothbuild.app.layout.Layout.SMOOTH_LOCK_PATH;
import static org.smoothbuild.app.report.PrintWriterReporter.printErrorToWriter;
import static org.smoothbuild.common.io.LockFile.lockFile;

import java.io.IOException;
import java.nio.channels.Channel;
import java.nio.channels.FileLock;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.Callable;

public abstract class ProjectCommand extends LoggingCommand implements Callable<Integer> {
  @Override
  public Integer call() {
    Path projectDir = Path.of(".");
    if (!Files.exists(projectDir.resolve(DEFAULT_MODULE_PATH.toString()))) {
      printError("Current directory doesn't have " + DEFAULT_MODULE_PATH.q()
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
      return executeCommand(normalizedProjectDir);
    } catch (IOException e) {
      printError("Error closing file lock.");
      return EXIT_CODE_ERROR;
    }
  }

  private void printError(String message) {
    printErrorToWriter(out(), message);
  }

  protected abstract Integer executeCommand(Path projectDir);
}
