package org.smoothbuild.common.bucket.base;

import java.io.IOException;
import okio.BufferedSink;
import okio.ByteString;
import org.junit.jupiter.api.BeforeEach;
import org.smoothbuild.common.bucket.mem.MemoryBucket;

public class SubBucketTest extends AbstractBucketTestSuite {
  private static final Path ROOT = Path.path("some/dir");
  private MemoryBucket parentBucket;

  @BeforeEach
  public void before() {
    parentBucket = new MemoryBucket();
    bucket = new SubBucket(parentBucket, ROOT);
  }

  @Override
  protected void createFile(Path path, ByteString content) throws IOException {
    try (BufferedSink sink = parentBucket.sink(ROOT.append(path))) {
      sink.write(content);
    }
  }

  @Override
  protected String resolve(Path path) {
    return ROOT.append(path).q();
  }
}
