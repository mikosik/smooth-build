package org.smoothbuild.common.filesystem.disk;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;

public class RecursiveDeleter {
  /**
   * Deletes file or dir recursively. Symbolic links are deleted but not
   * followed.
   */
  public static void deleteRecursively(Path path) throws IOException {
    if (Files.isDirectory(path) && !Files.isSymbolicLink(path)) {
      deleteDirContents(path);
    }

    /*
     * When file is a symbolic link then it is deleted without deleting a
     * file/dir it points to.
     */
    Files.delete(path);
  }

  private static void deleteDirContents(Path dir) throws IOException {
    try (DirectoryStream<Path> children = Files.newDirectoryStream(dir)) {
      for (Path child : children) {
        deleteRecursively(child);
      }
    }
  }
}
