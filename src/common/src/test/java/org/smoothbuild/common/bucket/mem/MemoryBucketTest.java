package org.smoothbuild.common.bucket.mem;

import java.io.IOException;
import okio.BufferedSink;
import okio.ByteString;
import org.junit.jupiter.api.BeforeEach;
import org.smoothbuild.common.bucket.base.AbstractBucketTestSuite;
import org.smoothbuild.common.bucket.base.Path;

public class MemoryBucketTest extends AbstractBucketTestSuite {
  @BeforeEach
  public void before() {
    bucket = new MemoryBucket();
  }

  // helpers

  @Override
  protected void createFile(Path path, ByteString content) throws IOException {
    try (BufferedSink sink = bucket.sink(path)) {
      sink.write(content);
    }
  }

  @Override
  protected String resolve(Path path) {
    return path.q();
  }
}
