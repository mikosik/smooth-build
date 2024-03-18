package org.smoothbuild.common.bucket.disk;

import static java.nio.file.Files.createDirectories;
import static okio.Okio.buffer;
import static okio.Okio.sink;

import java.io.IOException;
import okio.BufferedSink;
import okio.ByteString;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.io.TempDir;
import org.smoothbuild.common.bucket.base.AbstractBucketTestSuite;
import org.smoothbuild.common.bucket.base.Path;

public class DiskBucketTest extends AbstractBucketTestSuite {
  private java.nio.file.Path tempDir;

  @BeforeEach
  public void before(@TempDir java.nio.file.Path tempDir) {
    this.tempDir = tempDir.resolve("dir");
    bucket = new DiskBucket(this.tempDir);
  }

  @Override
  protected void createFile(Path path, ByteString content) throws IOException {
    java.nio.file.Path resolvedJdkPath = tempDir.resolve(path.toString());
    createDirectories(resolvedJdkPath.getParent());
    try (BufferedSink sink = buffer(sink(resolvedJdkPath))) {
      sink.write(content);
    }
  }

  @Override
  protected String resolve(Path path) {
    return path.q();
  }
}
