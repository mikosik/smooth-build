package org.smoothbuild.util;

import static java.nio.file.Files.createDirectories;
import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.WRITE;

import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Path;

import org.smoothbuild.cli.console.Console;

public class LockFile {
  public static FileLock lockFile(Path path) {
    if (path.getParent() != null) {
      Path dir = path.getParent();
      try {
        createDirectories(dir);
      } catch (FileAlreadyExistsException e) {
        printError("Cannot create " + dir + " directory - file with that name already exists.");
        return null;
      } catch (IOException e) {
        printError("Cannot create " + dir + " directory: " + e.getMessage());
        return null;
      }
    }

    try {
      FileLock lock = FileChannel.open(path, CREATE, WRITE).tryLock();
      if (lock == null) {
        printError("Another instance of smooth is running for this project.");
      }
      return lock;
    } catch (IOException e) {
      printError("IOException: " + e.getMessage());
      return null;
    }
  }

  private static void printError(String message) {
    new Console().error(message);
  }
}
