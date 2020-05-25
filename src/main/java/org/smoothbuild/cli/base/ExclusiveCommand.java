package org.smoothbuild.cli.base;

import static org.smoothbuild.SmoothConstants.EXIT_CODE_ERROR;
import static org.smoothbuild.SmoothConstants.SMOOTH_LOCK_PATH;
import static org.smoothbuild.SmoothConstants.USER_MODULE;
import static org.smoothbuild.cli.console.Console.printErrorToStream;
import static org.smoothbuild.util.LockFile.lockFile;

import java.io.IOException;
import java.nio.channels.Channel;
import java.nio.channels.FileLock;
import java.nio.file.Files;
import java.util.concurrent.Callable;

import picocli.CommandLine.Option;

public abstract class ExclusiveCommand extends LoggingCommand implements Callable<Integer> {
  @Option(
      names = { "--INTERNAL-use-lock-file" },
      hidden = true
  )
  protected boolean useLockFile = true;

  @Override
  public Integer call() {
    if (!Files.exists(USER_MODULE.smooth().path())) {
      printErrorToStream(out(), "Current dir doesn't have '" + USER_MODULE.smooth().path()
          + "'. Is it really smooth enabled project?");
      return EXIT_CODE_ERROR;
    }
    if (useLockFile) {
      FileLock fileLock = lockFile(out(), SMOOTH_LOCK_PATH.toJPath());
      if (fileLock == null) {
        return EXIT_CODE_ERROR;
      }
      Channel channel = fileLock.acquiredBy();
      try (channel) {
        return invokeCall();
      } catch (IOException e) {
        printErrorToStream(out(), "Error closing file lock.");
        return EXIT_CODE_ERROR;
      }
    } else {
      return invokeCall();
    }
  }

  protected abstract Integer invokeCall();
}
