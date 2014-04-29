package org.smoothbuild.io.fs.disk;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;

public class RecursiveDeleter {
  /**
   * Deletes file or directory recursively. Symbolic links are deleted but not
   * followed.
   */
  public static void deleteRecursively(java.nio.file.Path path) throws IOException {
    if (Files.isDirectory(path) && !Files.isSymbolicLink(path)) {
      deleteDirectoryContents(path);
    }

    /*
     * When file is a symbolic link then it is deleted without deleting a
     * file/dir it points to.
     */
    Files.delete(path);
  }

  private static void deleteDirectoryContents(java.nio.file.Path directory) throws IOException {
    try (DirectoryStream<Path> children = Files.newDirectoryStream(directory)) {
      for (Path child : children) {
        deleteRecursively(child);
      }
    }
  }
}
