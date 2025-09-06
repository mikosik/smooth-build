package org.smoothbuild.common.io;

import static java.nio.file.Files.createDirectories;
import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.WRITE;
import static org.smoothbuild.common.collect.Result.err;
import static org.smoothbuild.common.collect.Result.ok;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.channels.OverlappingFileLockException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Path;
import org.smoothbuild.common.collect.Result;

public class LockFile {
  private static final String ANOTHER_INSTANCE_IS_RUNNING =
      "Another instance of smooth is running for this project.";

  public static Result<FileLock> lockFile(PrintWriter out, Path path) {
    if (path.getParent() != null) {
      Path dir = path.getParent();
      try {
        createDirectories(dir);
      } catch (FileAlreadyExistsException e) {
        return err("Cannot create " + dir + " directory - file with that name already exists.");
      } catch (IOException e) {
        return err("Cannot create " + dir + " directory: " + e.getMessage());
      }
    }

    try {
      FileLock lock = FileChannel.open(path, CREATE, WRITE).tryLock();
      return lock == null ? err(ANOTHER_INSTANCE_IS_RUNNING) : ok(lock);
    } catch (OverlappingFileLockException e) {
      // Normally such exception means we have a bug (smooth tried to lock the same file twice).
      // However acceptance tests in fast-mode are run in single JVM.
      // This makes FileChannel.lock() throw exception instead of returning null when
      // file is already locked.
      return err(ANOTHER_INSTANCE_IS_RUNNING
          + "\nAnd it is running in the same JVM.\nOverlappingFileLockException: "
          + e.getMessage());
    } catch (IOException e) {
      return err("IOException: " + e.getMessage());
    }
  }
}
