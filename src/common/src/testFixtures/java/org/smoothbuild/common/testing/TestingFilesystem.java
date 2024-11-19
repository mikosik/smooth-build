package org.smoothbuild.common.testing;

import static okio.Okio.buffer;
import static org.smoothbuild.common.filesystem.base.FileSystemPart.fileSystemPart;
import static org.smoothbuild.common.filesystem.base.FullPath.fullPath;
import static org.smoothbuild.common.testing.TestingByteString.byteString;

import java.io.IOException;
import okio.BufferedSink;
import okio.ByteString;
import okio.Sink;
import okio.Source;
import org.smoothbuild.common.collect.List;
import org.smoothbuild.common.filesystem.base.FileSystem;
import org.smoothbuild.common.filesystem.base.FullPath;
import org.smoothbuild.common.filesystem.base.Path;
import org.smoothbuild.common.reflect.Classes;

public class TestingFilesystem {
  public static void createFile(FileSystem<FullPath> filesystem, FullPath path) throws IOException {
    createFile(filesystem, path, "");
  }

  public static void createFile(FileSystem<FullPath> filesystem, FullPath path, String content)
      throws IOException {
    TestingDirFileSystem.createFile(
        fileSystemPart(filesystem, fullPath(path.alias(), Path.root())), path.path(), content);
  }

  public static void createFile(FileSystem<FullPath> filesystem, FullPath path, Source content)
      throws IOException {
    TestingDirFileSystem.createFile(
        fileSystemPart(filesystem, fullPath(path.alias(), Path.root())), path.path(), content);
  }

  public static void writeFile(FileSystem<FullPath> filesystem, FullPath path) throws IOException {
    writeFile(filesystem, path, byteString("abc"));
  }

  public static void writeFile(FileSystem<FullPath> filesystem, FullPath path, ByteString content)
      throws IOException {
    try (BufferedSink sink = buffer(filesystem.sink(path))) {
      sink.write(content);
    }
  }

  public static ByteString readFile(FileSystem<FullPath> filesystem, FullPath path)
      throws IOException {
    try (var source = buffer(filesystem.source(path))) {
      return source.readByteString();
    }
  }

  public static void saveBytecodeInJar(
      FileSystem<FullPath> filesystem, FullPath fullPath, List<Class<?>> classes)
      throws IOException {
    try (Sink sink = buffer(filesystem.sink(fullPath))) {
      Classes.saveBytecodeInJar(sink, classes);
    }
  }
}
