package org.smoothbuild.common.testing;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.common.filesystem.base.Path.path;
import static org.smoothbuild.common.testing.TestingBucket.createFile;
import static org.smoothbuild.common.testing.TestingBucket.directoryToFileMap;

import java.util.Map;
import okio.ByteString;
import org.junit.jupiter.api.Test;
import org.smoothbuild.common.filesystem.mem.MemoryBucket;

public class TestingBucketTest {
  @Test
  void directory_to_file_map() throws Exception {
    var bucket = new MemoryBucket();
    var path1 = path("file1");
    var path2 = path("dir/file2");
    var content1 = ByteString.encodeUtf8("abc");
    var content2 = ByteString.encodeUtf8("def");
    createFile(bucket, path1, content1);
    createFile(bucket, path2, content2);

    assertThat(directoryToFileMap(bucket)).isEqualTo(Map.of(path1, content1, path2, content2));
  }
}
