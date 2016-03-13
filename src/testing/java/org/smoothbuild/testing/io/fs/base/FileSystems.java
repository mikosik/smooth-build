package org.smoothbuild.testing.io.fs.base;

import static org.smoothbuild.util.Streams.inputStreamToString;

import java.io.IOException;

import org.smoothbuild.io.fs.base.FileSystem;
import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.util.Streams;

public class FileSystems {
  public static Path createFile(FileSystem fileSystem, Path path, String content)
      throws IOException {
    Streams.writeAndClose(fileSystem.openOutputStream(path), content);
    return path;
  }

  public static String fileContent(FileSystem fileSystem, Path path) throws IOException {
    return inputStreamToString(fileSystem.openInputStream(path));
  }
}
