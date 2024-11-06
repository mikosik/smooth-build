package org.smoothbuild.common.bucket.base;

import static com.google.common.truth.Truth.assertThat;
import static okio.Okio.buffer;
import static org.smoothbuild.common.bucket.base.Alias.alias;
import static org.smoothbuild.common.bucket.base.FullPath.fullPath;
import static org.smoothbuild.common.bucket.base.Path.path;
import static org.smoothbuild.common.bucket.base.PathState.DIR;
import static org.smoothbuild.common.bucket.base.PathState.FILE;
import static org.smoothbuild.common.bucket.base.PathState.NOTHING;
import static org.smoothbuild.common.collect.Map.map;
import static org.smoothbuild.common.testing.TestingBucket.createFile;
import static org.smoothbuild.common.testing.TestingBucket.readFile;

import java.io.IOException;
import okio.ByteString;
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
    var bucketResolver = new BucketResolver(map(alias("project"), bucket));
    fileResolver = new FileResolver(bucketResolver);
  }

  @Nested
  class _sink {
    @Test
    void sink_writes_to_file() throws IOException {
      var path = path("file.txt");
      var content = "some string";
      var fullPath = fullPath(alias("project"), path);
      try (var sink = buffer(fileResolver.sink(fullPath))) {
        sink.writeUtf8(content);
      }

      assertThat(readFile(bucket, path)).isEqualTo(ByteString.encodeUtf8(content));
    }
  }

  @Nested
  class _path_state {
    @Test
    void of_file() throws IOException {
      var path = path("file.txt");
      createFile(bucket, path, "some string");
      assertThat(fileResolver.pathState(fullPath(alias("project"), path))).isEqualTo(FILE);
    }

    @Test
    void of_directory() throws IOException {
      var path = path("directory");
      bucket.createDir(path);
      assertThat(fileResolver.pathState(fullPath(alias("project"), path))).isEqualTo(DIR);
    }

    @Test
    void of_nothing() {
      var path = path("file.txt");
      assertThat(fileResolver.pathState(fullPath(alias("project"), path))).isEqualTo(NOTHING);
    }
  }
}
