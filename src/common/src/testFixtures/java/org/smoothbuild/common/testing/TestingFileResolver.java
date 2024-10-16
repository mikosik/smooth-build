package org.smoothbuild.common.testing;

import static okio.Okio.buffer;

import java.io.IOException;
import okio.Sink;
import org.smoothbuild.common.bucket.base.FileResolver;
import org.smoothbuild.common.bucket.base.FullPath;
import org.smoothbuild.common.collect.List;
import org.smoothbuild.common.reflect.Classes;

public class TestingFileResolver {
  public static void saveBytecodeInJar(
      FileResolver fileResolver, FullPath fullPath, List<Class<?>> classes) throws IOException {
    try (Sink sink = buffer(fileResolver.sink(fullPath))) {
      Classes.saveBytecodeInJar(sink, classes);
    }
  }
}
