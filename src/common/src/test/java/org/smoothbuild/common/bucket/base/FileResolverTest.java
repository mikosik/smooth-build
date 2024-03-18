package org.smoothbuild.common.bucket.base;

import static com.google.common.truth.Truth.assertThat;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.smoothbuild.common.bucket.base.FullPath.fullPath;
import static org.smoothbuild.common.bucket.base.Path.path;
import static org.smoothbuild.common.bucket.base.PathState.DIR;
import static org.smoothbuild.common.bucket.base.PathState.FILE;
import static org.smoothbuild.common.bucket.base.PathState.NOTHING;
import static org.smoothbuild.common.collect.Map.map;
import static org.smoothbuild.common.testing.TestingBucketId.bucketId;

import java.io.IOException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.smoothbuild.common.bucket.mem.MemoryBucket;

public class FileResolverTest {
  private MemoryBucket bucket;
  private FileResolver fileResolver;

  @BeforeEach
  public void setUp() {
    bucket = new MemoryBucket();
    fileResolver = new FileResolver(map(bucketId("project"), bucket));
  }

  @Nested
  class _contentOf {
    @Test
    void contentOf() throws IOException {
      var path = path("file.txt");
      var content = "some string";
      createFile(path, content);
      assertThat(fileResolver.contentOf(fullPath(bucketId("project"), path), UTF_8))
          .isEqualTo(content);
    }
  }

  @Nested
  class _path_state {
    @Test
    public void of_file() throws IOException {
      var path = path("file.txt");
      createFile(path, "some string");
      assertThat(fileResolver.pathState(fullPath(bucketId("project"), path))).isEqualTo(FILE);
    }

    @Test
    public void of_directory() throws IOException {
      var path = path("directory");
      bucket.createDir(path);
      assertThat(fileResolver.pathState(fullPath(bucketId("project"), path))).isEqualTo(DIR);
    }

    @Test
    public void of_nothing() {
      var path = path("file.txt");
      assertThat(fileResolver.pathState(fullPath(bucketId("project"), path))).isEqualTo(NOTHING);
    }
  }

  private void createFile(Path path, String string) throws IOException {
    try (var bufferedSink = bucket.sink(path)) {
      bufferedSink.writeUtf8(string);
    }
  }
}
