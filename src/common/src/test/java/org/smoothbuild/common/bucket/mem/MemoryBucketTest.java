package org.smoothbuild.common.bucket.mem;

import static okio.Okio.buffer;

import java.io.IOException;
import okio.BufferedSink;
import okio.ByteString;
import org.junit.jupiter.api.BeforeEach;
import org.smoothbuild.common.bucket.base.AbstractBucketTestSuite;
import org.smoothbuild.common.bucket.base.Path;

public class MemoryBucketTest extends AbstractBucketTestSuite {
  @BeforeEach
  public void before() throws IOException {
    bucket = new MemoryBucket();
    bucket.createDir(Path.root());
  }

  // helpers

  @Override
  protected void createFile(Path path, ByteString content) throws IOException {
    bucket.createDir(path.parent());
    try (BufferedSink sink = buffer(bucket.sink(path))) {
      sink.write(content);
    }
  }

  @Override
  protected void createDir(Path path) throws IOException {
    bucket.createDir(path);
  }

  @Override
  protected String resolve(Path path) {
    return path.q();
  }
}
