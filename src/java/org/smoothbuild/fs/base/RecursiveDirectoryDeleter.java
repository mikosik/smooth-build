package org.smoothbuild.fs.base;

import static com.google.common.base.Preconditions.checkArgument;

import java.io.File;
import java.io.IOException;

public class RecursiveDirectoryDeleter {
  public static void deleteRecursively(File file) throws IOException {
    if (file.isDirectory()) {
      deleteDirectoryContents(file);
    }
    if (!file.delete()) {
      throw new IOException("Failed to delete " + file);
    }
  }

  private static void deleteDirectoryContents(File directory) throws IOException {
    checkArgument(directory.isDirectory(), "Not a directory: %s", directory);
    // Symbolic links will have different canonical and absolute paths
    if (!directory.getCanonicalPath().equals(directory.getAbsolutePath())) {
      return;
    }
    File[] files = directory.listFiles();
    if (files == null) {
      throw new IOException("Error listing files for " + directory);
    }
    for (File file : files) {
      deleteRecursively(file);
    }
  }
}
