package org.smoothbuild.common.testing;

import static okio.Okio.buffer;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import okio.ByteString;
import okio.Sink;
import okio.Source;
import org.smoothbuild.common.collect.List;
import org.smoothbuild.common.filesystem.base.FileSystem;
import org.smoothbuild.common.filesystem.base.FullPath;
import org.smoothbuild.common.filesystem.base.Path;
import org.smoothbuild.common.filesystem.base.PathI;
import org.smoothbuild.common.reflect.Classes;

public class TestingFileSystem {
  public static Map<Path, ByteString> directoryToFileMap(
      FileSystem<FullPath> fileSystem, FullPath path) throws IOException {
    var result = new HashMap<Path, ByteString>();
    var pathIterator = fileSystem.filesRecursively(path);
    while (pathIterator.hasNext()) {
      var current = pathIterator.next();
      result.put(current, readFile(fileSystem, path.append(current)));
    }
    return result;
  }

  public static <P extends PathI<P>> void createFile(FileSystem<P> fileSystem, P path)
      throws IOException {
    fileSystem.createDir(path.parent());
    writeFile(fileSystem, path);
  }

  public static <P extends PathI<P>> void createFile(
      FileSystem<P> fileSystem, P path, String content) throws IOException {
    fileSystem.createDir(path.parent());
    writeFile(fileSystem, path, content);
  }

  public static <P extends PathI<P>> void createFile(
      FileSystem<P> fileSystem, P path, ByteString content) throws IOException {
    fileSystem.createDir(path.parent());
    writeFile(fileSystem, path, content);
  }

  public static <P extends PathI<P>> void createFile(
      FileSystem<P> fileSystem, P path, Source content) throws IOException {
    fileSystem.createDir(path.parent());
    writeFile(fileSystem, path, content);
  }

  public static <P extends PathI<P>> void writeFile(FileSystem<P> fileSystem, P path)
      throws IOException {
    writeFile(fileSystem, path, "");
  }

  public static <P extends PathI<P>> void writeFile(
      FileSystem<P> fileSystem, P path, String content) throws IOException {
    writeFile(fileSystem, path, ByteString.encodeUtf8(content));
  }

  public static <P extends PathI<P>> void writeFile(
      FileSystem<P> fileSystem, P path, ByteString content) throws IOException {
    try (var bufferedSink = buffer(fileSystem.sink(path))) {
      bufferedSink.write(content);
    }
  }

  public static <P extends PathI<P>> void writeFile(
      FileSystem<P> fileSystem, P path, Source content) throws IOException {
    try (var bufferedSink = buffer(fileSystem.sink(path))) {
      bufferedSink.writeAll(content);
    }
  }

  public static <P extends PathI<P>> ByteString readFile(FileSystem<P> fileSystem, P path)
      throws IOException {
    try (var source = buffer(fileSystem.source(path))) {
      return source.readByteString();
    }
  }

  public static <P extends PathI<P>> void saveBytecodeInJar(
      FileSystem<P> fileSystem, P fullPath, List<Class<?>> classes) throws IOException {
    try (Sink sink = buffer(fileSystem.sink(fullPath))) {
      Classes.saveBytecodeInJar(sink, classes);
    }
  }
}
