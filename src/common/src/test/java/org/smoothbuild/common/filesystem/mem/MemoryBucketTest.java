package org.smoothbuild.common.filesystem.mem;

import static okio.Okio.buffer;

import java.io.IOException;
import okio.BufferedSink;
import okio.ByteString;
import org.junit.jupiter.api.BeforeEach;
import org.smoothbuild.common.filesystem.base.AbstractBucketTestSuite;
import org.smoothbuild.common.filesystem.base.Path;

public class MemoryBucketTest extends AbstractBucketTestSuite {
  @BeforeEach
  public void before() throws IOException {
    fileSystem = new MemoryBucket();
    fileSystem.createDir(Path.root());
  }

  // helpers

  @Override
  protected void createFile(Path path, ByteString content) throws IOException {
    fileSystem.createDir(path.parent());
    try (BufferedSink sink = buffer(fileSystem.sink(path))) {
      sink.write(content);
    }
  }

  @Override
  protected void createDir(Path path) throws IOException {
    fileSystem.createDir(path);
  }

  @Override
  protected String resolve(Path path) {
    return path.q();
  }
}
