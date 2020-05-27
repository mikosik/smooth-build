package org.smoothbuild.cli.base;

import static org.smoothbuild.SmoothConstants.EXIT_CODE_ERROR;
import static org.smoothbuild.cli.console.Console.printErrorToStream;
import static org.smoothbuild.install.ProjectPaths.SMOOTH_LOCK_PATH;
import static org.smoothbuild.install.ProjectPaths.USER_MODULE_FILE_NAME;
import static org.smoothbuild.install.ProjectPaths.USER_MODULE_PATH;
import static org.smoothbuild.util.LockFile.lockFile;

import java.io.IOException;
import java.nio.channels.Channel;
import java.nio.channels.FileLock;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.Callable;

import picocli.CommandLine.Option;

public abstract class ExclusiveCommand extends LoggingCommand implements Callable<Integer> {
  @Option(
      names = { "--project-dir", "-d" },
      defaultValue = ".",
      description = "Project directory where '" + USER_MODULE_FILE_NAME
          + "' is located. By default equal to current directory.\n"
  )
  private Path projectDir;

  @Option(
      names = { "--INTERNAL-use-lock-file" },
      hidden = true
  )
  protected boolean useLockFile = true;

  @Override
  public Integer call() {
    if (!Files.exists(projectDir)) {
      printError("Directory '" + projectDir + "' specified via '--project-dir/-d' doesn't exist.");
      return EXIT_CODE_ERROR;
    }
    if (!Files.exists(projectDir.resolve(USER_MODULE_PATH.toString()))) {
      printError("Directory '" + projectDir + "' doesn't have " + USER_MODULE_PATH.q()
          + ". Is it really smooth enabled project?");
      return EXIT_CODE_ERROR;
    }
    Path normalizedProjectDir = projectDir.normalize();
    if (useLockFile) {
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
    } else {
      return executeCommand(normalizedProjectDir);
    }
  }

  private void printError(String message) {
    printErrorToStream(out(), message);
  }

  protected abstract Integer executeCommand(Path projectDir);
}
