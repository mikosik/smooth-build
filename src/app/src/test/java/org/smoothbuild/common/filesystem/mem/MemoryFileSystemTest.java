package org.smoothbuild.common.filesystem.mem;

import java.io.IOException;

import org.junit.jupiter.api.BeforeEach;
import org.smoothbuild.common.filesystem.base.AbstractFileSystemTestSuite;
import org.smoothbuild.common.filesystem.base.PathS;

import okio.BufferedSink;
import okio.ByteString;

public class MemoryFileSystemTest extends AbstractFileSystemTestSuite {
  @BeforeEach
  public void before() {
    fileSystem = new MemoryFileSystem();
  }

  // helpers

  @Override
  protected void createFile(PathS path, ByteString content) throws IOException {
    try (BufferedSink sink = fileSystem.sink(path)) {
      sink.write(content);
    }
  }

  @Override
  protected String resolve(PathS path) {
    return path.q();
  }
}
