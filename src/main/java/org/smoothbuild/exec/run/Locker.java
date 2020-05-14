package org.smoothbuild.exec.run;

import static java.nio.file.Files.createDirectories;
import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.WRITE;
import static org.smoothbuild.SmoothConstants.SMOOTH_LOCK_PATH;
import static org.smoothbuild.install.InstallationPaths.USER_MODULE;

import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.smoothbuild.cli.console.Console;

public class Locker {
  public static boolean tryAcquireLock() {
    if (!Files.exists(USER_MODULE.smooth().path())) {
      printError("Current dir doesn't have '" + USER_MODULE.smooth().path()
          + "'. Is it really smooth enabled project?");
      return false;
    }
    String error = acquireFileLock(SMOOTH_LOCK_PATH.toJPath());
    if (error != null) {
      printError(error);
      return false;
    }
    return true;
  }

  private static void printError(String message) {
    new Console().error(message);
  }

  // Visible for testing
  static String acquireFileLock(Path path) {
    if (path.getParent() != null) {
      Path dir = path.getParent();
      try {
        createDirectories(dir);
      } catch (FileAlreadyExistsException e) {
        return "Cannot create " + dir + " directory - file with that name exists already.";
      } catch (IOException e) {
        return "Cannot create " + dir + " directory: " + e.getMessage();
      }
    }

    try {
      FileLock lock = FileChannel.open(path, CREATE, WRITE).tryLock();
      if (lock == null) {
        return "Another instance of smooth is running for this project.";
      }
    } catch (IOException e) {
      return "IOException: " + e.getMessage();
    }
    return null;
  }
}
