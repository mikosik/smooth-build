package org.smoothbuild.common.bucket.disk;

import static java.nio.file.Files.createDirectories;
import static okio.Okio.buffer;
import static okio.Okio.sink;

import java.io.IOException;
import java.nio.file.Files;
import okio.BufferedSink;
import okio.ByteString;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.io.TempDir;
import org.smoothbuild.common.bucket.base.AbstractBucketTestSuite;
import org.smoothbuild.common.bucket.base.Path;

public class DiskBucketTest extends AbstractBucketTestSuite {
  private java.nio.file.Path bucketDir;

  @BeforeEach
  public void before(@TempDir java.nio.file.Path tempDir) throws IOException {
    bucketDir = tempDir.resolve("dir");
    Files.createDirectories(bucketDir);
    bucket = new DiskBucket(bucketDir);
  }

  @Override
  protected void createFile(Path path, ByteString content) throws IOException {
    createDir(path.parent());
    try (BufferedSink sink = buffer(sink(toJdkPath(path)))) {
      sink.write(content);
    }
  }

  @Override
  protected void createDir(Path path) throws IOException {
    createDirectories(toJdkPath(path));
  }

  private java.nio.file.Path toJdkPath(Path path) {
    return bucketDir.resolve(path.toString());
  }

  @Override
  protected String resolve(Path path) {
    return path.q();
  }
}
