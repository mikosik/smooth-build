package org.smoothbuild.common.testing;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import okio.ByteString;
import okio.Source;
import org.smoothbuild.common.filesystem.base.FileSystem;
import org.smoothbuild.common.filesystem.base.Path;
import org.smoothbuild.common.filesystem.base.PathIterator;
import org.smoothbuild.common.filesystem.base.RecursivePathsIterator;

public class TestingFileSystem {
  public static Map<Path, ByteString> directoryToFileMap(FileSystem fileSystem, Path dir)
      throws IOException {
    var result = new HashMap<Path, ByteString>();
    PathIterator pathIterator = RecursivePathsIterator.recursivePathsIterator(fileSystem, dir);
    while (pathIterator.hasNext()) {
      var path = pathIterator.next();
      result.put(path, readFile(fileSystem, path));
    }
    return result;
  }

  public static void writeFile(FileSystem fileSystem, Path path, String content)
      throws IOException {
    writeFile(fileSystem, path, ByteString.encodeUtf8(content));
  }

  public static void writeFile(FileSystem fileSystem, Path path, ByteString content)
      throws IOException {
    try (var bufferedSink = fileSystem.sink(path)) {
      bufferedSink.write(content);
    }
  }

  public static void writeFile(FileSystem fileSystem, Path path, Source content)
      throws IOException {
    try (var bufferedSink = fileSystem.sink(path)) {
      bufferedSink.writeAll(content);
    }
  }

  public static ByteString readFile(FileSystem fileSystem, Path path) throws IOException {
    try (var source = fileSystem.source(path)) {
      return source.readByteString();
    }
  }
}
