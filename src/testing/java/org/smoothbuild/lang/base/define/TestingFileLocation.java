package org.smoothbuild.lang.base.define;

import static org.smoothbuild.lang.base.define.Space.USER;
import static org.smoothbuild.util.Lists.list;

import java.nio.file.Path;

public class TestingFileLocation {
  public static final Path BUILD_FILE_PATH = Path.of("myBuild.smooth");
  private static final Path IMPORTED_FILE_PATH = Path.of("imported.smooth");

  public static FileLocation fileLocation() {
    return new SModule(USER, BUILD_FILE_PATH, list()).smoothFile();
  }

  public static FileLocation importedFileLocation() {
    return new SModule(USER, IMPORTED_FILE_PATH, list()).smoothFile();
  }
}
