package org.smoothbuild.util;

import static java.nio.file.Files.createDirectories;
import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.WRITE;
import static org.smoothbuild.cli.console.Console.printErrorToStream;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Path;

public class LockFile {
  public static FileLock lockFile(PrintWriter out, Path path) {
    if (path.getParent() != null) {
      Path dir = path.getParent();
      try {
        createDirectories(dir);
      } catch (FileAlreadyExistsException e) {
        printErrorToStream(
            out, "Cannot create " + dir + " directory - file with that name already exists.");
        return null;
      } catch (IOException e) {
        printErrorToStream(out, "Cannot create " + dir + " directory: " + e.getMessage());
        return null;
      }
    }

    try {
      FileLock lock = FileChannel.open(path, CREATE, WRITE).tryLock();
      if (lock == null) {
        printErrorToStream(out, "Another instance of smooth is running for this project.");
      }
      return lock;
    } catch (IOException e) {
      printErrorToStream(out, "IOException: " + e.getMessage());
      return null;
    }
  }
}
