package org.smoothbuild.common.filesystem.base;

import static com.google.common.truth.Truth.assertThat;
import static okio.Okio.buffer;
import static org.smoothbuild.common.filesystem.base.SubBucket.subBucket;

import java.io.IOException;
import okio.BufferedSink;
import okio.ByteString;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.smoothbuild.common.filesystem.mem.MemoryBucket;

public class SubBucketTest extends AbstractBucketTestSuite {
  private static final Path subDir = Path.path("some/dir");
  private MemoryBucket superBucket;

  @BeforeEach
  public void before() throws IOException {
    superBucket = new MemoryBucket();
    superBucket.createDir(subDir);
    bucket = subBucket(superBucket, subDir);
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

  @Test
  void factory_method_not_creates_new_bucket_instance_when_path_is_root() {
    assertThat(subBucket(superBucket, Path.root())).isSameInstanceAs(superBucket);
  }

  private static Path superPath(Path path) {
    return subDir.append(path);
  }
}
