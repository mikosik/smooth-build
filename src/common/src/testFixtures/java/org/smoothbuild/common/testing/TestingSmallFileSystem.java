package org.smoothbuild.common.testing;

import static okio.Okio.buffer;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import okio.ByteString;
import okio.Source;
import org.smoothbuild.common.filesystem.base.FileSystem;
import org.smoothbuild.common.filesystem.base.Path;
import org.smoothbuild.common.filesystem.base.PathIterator;
import org.smoothbuild.common.filesystem.base.RecursivePathsIterator;

public class TestingSmallFileSystem {
  public static Map<Path, ByteString> directoryToFileMap(FileSystem<Path> fileSystem)
      throws IOException {
    var result = new HashMap<Path, ByteString>();
    PathIterator pathIterator =
        RecursivePathsIterator.recursivePathsIterator(fileSystem, Path.root());
    while (pathIterator.hasNext()) {
      var path = pathIterator.next();
      result.put(path, readFile(fileSystem, path));
    }
    return result;
  }

  public static void createFile(FileSystem<Path> fileSystem, Path path, String content)
      throws IOException {
    fileSystem.createDir(path.parent());
    writeFile(fileSystem, path, content);
  }

  public static void createFile(FileSystem<Path> fileSystem, Path path, ByteString content)
      throws IOException {
    fileSystem.createDir(path.parent());
    writeFile(fileSystem, path, content);
  }

  public static void createFile(FileSystem<Path> fileSystem, Path path, Source content)
      throws IOException {
    fileSystem.createDir(path.parent());
    writeFile(fileSystem, path, content);
  }

  private static void writeFile(FileSystem<Path> fileSystem, Path path, String content)
      throws IOException {
    writeFile(fileSystem, path, ByteString.encodeUtf8(content));
  }

  public static void writeFile(FileSystem<Path> fileSystem, Path path, ByteString content)
      throws IOException {
    try (var bufferedSink = buffer(fileSystem.sink(path))) {
      bufferedSink.write(content);
    }
  }

  public static void writeFile(FileSystem<Path> fileSystem, Path path, Source content)
      throws IOException {
    try (var bufferedSink = buffer(fileSystem.sink(path))) {
      bufferedSink.writeAll(content);
    }
  }

  public static ByteString readFile(FileSystem<Path> fileSystem, Path path) throws IOException {
    try (var source = buffer(fileSystem.source(path))) {
      return source.readByteString();
    }
  }
}
