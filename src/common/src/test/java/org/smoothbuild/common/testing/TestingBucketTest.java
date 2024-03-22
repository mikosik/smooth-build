package org.smoothbuild.common.testing;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.common.bucket.base.Path.path;
import static org.smoothbuild.common.testing.TestingBucket.directoryToFileMap;
import static org.smoothbuild.common.testing.TestingBucket.writeFile;

import java.util.Map;
import okio.ByteString;
import org.junit.jupiter.api.Test;
import org.smoothbuild.common.bucket.mem.MemoryBucket;

public class TestingBucketTest {
  @Test
  void directory_to_file_map() throws Exception {
    var bucket = new MemoryBucket();
    var dir = path("dir");
    var path1 = path("file1");
    var path2 = path("file2");
    var content1 = ByteString.encodeUtf8("abc");
    var content2 = ByteString.encodeUtf8("def");
    writeFile(bucket, dir.append(path1), content1);
    writeFile(bucket, dir.append(path2), content2);

    assertThat(directoryToFileMap(bucket, dir)).isEqualTo(Map.of(path1, content1, path2, content2));
  }
}