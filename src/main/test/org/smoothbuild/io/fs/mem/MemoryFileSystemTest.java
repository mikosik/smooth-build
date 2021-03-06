package org.smoothbuild.io.fs.mem;

import java.io.IOException;

import org.junit.jupiter.api.BeforeEach;
import org.smoothbuild.io.fs.base.GenericFileSystemTestCase;
import org.smoothbuild.io.fs.base.Path;

import okio.BufferedSink;
import okio.ByteString;

public class MemoryFileSystemTest extends GenericFileSystemTestCase {
  @BeforeEach
  public void before() {
    fileSystem = new MemoryFileSystem();
  }

  // helpers

  @Override
  protected void createFile(Path path, ByteString content) throws IOException {
    try (BufferedSink sink = fileSystem.sink(path)) {
      sink.write(content);
    }
  }
}
