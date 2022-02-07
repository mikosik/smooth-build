package org.smoothbuild.util.io;

import static java.nio.file.Files.createDirectories;
import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.WRITE;
import static org.smoothbuild.out.console.Console.printErrorToWriter;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.channels.OverlappingFileLockException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Path;

public class LockFile {
  private static final String ANOTHER_INSTANCE_IS_RUNNING =
      "Another instance of smooth is running for this project.";

  public static FileLock lockFile(PrintWriter out, Path path) {
    if (path.getParent() != null) {
      Path dir = path.getParent();
      try {
        createDirectories(dir);
      } catch (FileAlreadyExistsException e) {
        printErrorToWriter(
            out, "Cannot create " + dir + " directory - file with that name already exists.");
        return null;
      } catch (IOException e) {
        printErrorToWriter(out, "Cannot create " + dir + " directory: " + e.getMessage());
        return null;
      }
    }

    try {
      FileLock lock = FileChannel.open(path, CREATE, WRITE).tryLock();
      if (lock == null) {
        printErrorToWriter(out, ANOTHER_INSTANCE_IS_RUNNING);
      }
      return lock;
    } catch (OverlappingFileLockException e) {
      // Normally such exception means we have a bug (smooth tried to lock the same file twice).
      // However acceptance tests in fast-mode are run in single JVM.
      // This makes FileChannel.lock() throw exception instead of returning null when
      // file is already locked.
      printErrorToWriter(
          out, ANOTHER_INSTANCE_IS_RUNNING + "\n And it is running in the same JVM.");
      printErrorToWriter(out, "OverlappingFileLockException: " + e.getMessage());
      return null;
    } catch (IOException e) {
      printErrorToWriter(out, "IOException: " + e.getMessage());
      return null;
    }
  }
}
