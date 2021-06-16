package org.smoothbuild.lang.base.define;

import static org.smoothbuild.lang.base.define.Space.USER;

import java.nio.file.Path;

public class TestingFileLocation {
  public static final String BUILD_FILE_PATH = "myBuild.smooth";
  private static final String IMPORTED_FILE_PATH = "imported.smooth";

  public static FileLocation fileLocation() {
    return fileLocation(BUILD_FILE_PATH);
  }

  public static FileLocation nativeFileLocation() {
    return fileLocation(BUILD_FILE_PATH).withExtension("jar");
  }

  public static FileLocation importedFileLocation() {
    return fileLocation(IMPORTED_FILE_PATH);
  }

  public static FileLocation fileLocation(String filePath) {
    return new FileLocation(USER, Path.of(filePath));
  }
}
