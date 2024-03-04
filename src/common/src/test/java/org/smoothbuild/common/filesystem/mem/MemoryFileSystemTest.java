package org.smoothbuild.common.filesystem.mem;

import java.io.IOException;
import okio.BufferedSink;
import okio.ByteString;
import org.junit.jupiter.api.BeforeEach;
import org.smoothbuild.common.filesystem.base.AbstractFileSystemTestSuite;
import org.smoothbuild.common.filesystem.base.Path;

public class MemoryFileSystemTest extends AbstractFileSystemTestSuite {
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

  @Override
  protected String resolve(Path path) {
    return path.q();
  }
}
