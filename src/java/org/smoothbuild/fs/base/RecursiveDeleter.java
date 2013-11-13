package org.smoothbuild.fs.base;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;

public class RecursiveDeleter {
  public static void deleteRecursively(java.nio.file.Path file) throws IOException {
    if (Files.isDirectory(file) && !Files.isSymbolicLink(file)) {
      deleteDirectoryContents(file);
    }

    /*
     * When file is a symbolic link then it is deleted without deleting a
     * file/dir it points to.
     */
    Files.delete(file);
  }

  private static void deleteDirectoryContents(java.nio.file.Path file) throws IOException {
    try (DirectoryStream<Path> children = Files.newDirectoryStream(file)) {
      for (Path child : children) {
        deleteRecursively(child);
      }
    }
  }
}
