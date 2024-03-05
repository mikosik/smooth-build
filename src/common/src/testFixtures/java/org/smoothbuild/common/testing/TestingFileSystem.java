package org.smoothbuild.common.testing;

import java.io.IOException;
import okio.Source;
import org.smoothbuild.common.filesystem.base.FileSystem;
import org.smoothbuild.common.filesystem.base.Path;

public class TestingFileSystem {
  public static void writeFile(FileSystem fileSystem, Path path, String content)
      throws IOException {
    try (var bufferedSink = fileSystem.sink(path)) {
      bufferedSink.writeUtf8(content);
    }
  }

  public static void writeFile(FileSystem fileSystem, Path path, Source content)
      throws IOException {
    try (var bufferedSink = fileSystem.sink(path)) {
      bufferedSink.writeAll(content);
    }
  }
}
