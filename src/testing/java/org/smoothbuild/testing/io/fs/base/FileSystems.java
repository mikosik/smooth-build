package org.smoothbuild.testing.io.fs.base;

import java.io.IOException;

import org.smoothbuild.io.fs.base.FileSystem;
import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.testing.common.StreamTester;

public class FileSystems {
  public static Path createFile(FileSystem fileSystem, Path path, String content)
      throws IOException {
    StreamTester.writeAndClose(fileSystem.openOutputStream(path), content);
    return path;
  }
}
