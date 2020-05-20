package org.smoothbuild.cli.base;

import static org.smoothbuild.SmoothConstants.EXIT_CODE_ERROR;
import static org.smoothbuild.SmoothConstants.SMOOTH_LOCK_PATH;
import static org.smoothbuild.SmoothConstants.USER_MODULE;
import static org.smoothbuild.util.LockFile.lockFile;

import java.io.IOException;
import java.nio.channels.Channel;
import java.nio.channels.FileLock;
import java.nio.file.Files;
import java.util.function.Supplier;

import org.smoothbuild.cli.console.Console;

public class RunExclusively {
  public static int runExclusively(Supplier<Integer> action) {
    Console console = new Console();
    if (!Files.exists(USER_MODULE.smooth().path())) {
      console.error("Current dir doesn't have '" + USER_MODULE.smooth().path()
          + "'. Is it really smooth enabled project?");
      return EXIT_CODE_ERROR;
    }
    FileLock fileLock = lockFile(SMOOTH_LOCK_PATH.toJPath());
    if (fileLock == null) {
      return EXIT_CODE_ERROR;
    }
    Channel channel = fileLock.acquiredBy();
    try (channel) {
      return action.get();
    } catch (IOException e) {
      console.error("Error closing file lock.");
      return EXIT_CODE_ERROR;
    }
  }
}
