package org.smoothbuild.virtualmachine.bytecode.load;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.common.bucket.base.BucketId.bucketId;
import static org.smoothbuild.common.bucket.base.FullPath.fullPath;
import static org.smoothbuild.common.bucket.base.Path.path;
import static org.smoothbuild.common.collect.Map.map;
import static org.smoothbuild.common.testing.TestingBucket.createFile;

import org.junit.jupiter.api.Test;
import org.smoothbuild.common.bucket.base.FileResolver;
import org.smoothbuild.common.bucket.mem.MemoryBucket;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BBlob;
import org.smoothbuild.virtualmachine.testing.BytecodeTestContext;

public class FileContentReaderTest extends BytecodeTestContext {
  @Test
  void read_returns_file_content() throws Exception {
    var bucket = new MemoryBucket();
    var bucketId = bucketId("project");
    var fileContentReader =
        new FileContentReader(new FileResolver(map(bucketId, bucket)), exprDb());
    var path = path("my/file");
    var content = "file content";
    createFile(bucket, path, content);

    BBlob blob = fileContentReader.read(fullPath(bucketId, path));

    assertThat(blob).isEqualTo(bBlob(content));
  }

  @Test
  void read_caches_file_content() throws Exception {
    var bucket = new MemoryBucket();
    var bucketId = bucketId("project");
    var fileContentReader =
        new FileContentReader(new FileResolver(map(bucketId, bucket)), exprDb());
    var path = path("my/file");
    var content = "file content";
    createFile(bucket, path, content);

    BBlob blob = fileContentReader.read(fullPath(bucketId, path));
    bucket.delete(path);
    BBlob cached = fileContentReader.read(fullPath(bucketId, path));

    assertThat(cached).isEqualTo(blob);
  }
}
