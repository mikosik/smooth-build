package org.smoothbuild.common.bucket.base;

import static okio.Okio.buffer;

import java.io.IOException;
import okio.BufferedSink;
import okio.ByteString;
import org.junit.jupiter.api.BeforeEach;
import org.smoothbuild.common.bucket.mem.MemoryBucket;

public class SubBucketTest extends AbstractBucketTestSuite {
  private static final Path subDir = Path.path("some/dir");
  private MemoryBucket superBucket;

  @BeforeEach
  public void before() throws IOException {
    superBucket = new MemoryBucket();
    superBucket.createDir(subDir);
    bucket = new SubBucket(superBucket, subDir);
  }

  @Override
  protected void createFile(Path path, ByteString content) throws IOException {
    createDir(path.parent());
    try (BufferedSink sink = buffer(superBucket.sink(superPath(path)))) {
      sink.write(content);
    }
  }

  @Override
  protected void createDir(Path path) throws IOException {
    superBucket.createDir(superPath(path));
  }

  @Override
  protected String resolve(Path path) {
    return superPath(path).q();
  }

  private static Path superPath(Path path) {
    return subDir.append(path);
  }
}
