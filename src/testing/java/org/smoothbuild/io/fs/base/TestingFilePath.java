package org.smoothbuild.io.fs.base;

import static org.smoothbuild.io.fs.base.PathS.path;
import static org.smoothbuild.io.fs.space.Space.PRJ;

import org.smoothbuild.io.fs.space.FilePath;

public class TestingFilePath {
  public static final String BUILD_FILE_PATH = "myBuild.smooth";
  private static final String IMPORTED_FILE_PATH = "imported.smooth";

  public static FilePath filePath() {
    return smoothFilePath();
  }

  public static FilePath smoothFilePath() {
    return filePath(BUILD_FILE_PATH);
  }

  public static FilePath nativeFilePath() {
    return smoothFilePath().withExtension("jar");
  }

  public static FilePath importedFilePath() {
    return filePath(IMPORTED_FILE_PATH);
  }

  public static FilePath filePath(String filePath) {
    return new FilePath(PRJ, path(filePath));
  }
}
