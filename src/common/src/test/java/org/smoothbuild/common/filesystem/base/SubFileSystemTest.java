package org.smoothbuild.common.filesystem.base;

import java.io.IOException;
import okio.BufferedSink;
import okio.ByteString;
import org.junit.jupiter.api.BeforeEach;
import org.smoothbuild.common.filesystem.mem.MemoryFileSystem;

public class SubFileSystemTest extends AbstractFileSystemTestSuite {
  private static final Path ROOT = Path.path("some/dir");
  private MemoryFileSystem rootFileSystem;

  @BeforeEach
  public void before() {
    rootFileSystem = new MemoryFileSystem();
    fileSystem = new SubFileSystem(rootFileSystem, ROOT);
  }

  @Override
  protected void createFile(Path path, ByteString content) throws IOException {
    try (BufferedSink sink = rootFileSystem.sink(ROOT.append(path))) {
      sink.write(content);
    }
  }

  @Override
  protected String resolve(Path path) {
    return ROOT.append(path).q();
  }
}
