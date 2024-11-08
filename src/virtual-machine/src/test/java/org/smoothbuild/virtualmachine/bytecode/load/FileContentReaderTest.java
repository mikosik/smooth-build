package org.smoothbuild.virtualmachine.bytecode.load;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.common.bucket.base.FullPath.fullPath;
import static org.smoothbuild.common.bucket.base.Path.path;
import static org.smoothbuild.common.collect.Map.map;
import static org.smoothbuild.common.testing.TestingBucket.createFile;

import org.junit.jupiter.api.Test;
import org.smoothbuild.common.bucket.base.Filesystem;
import org.smoothbuild.common.bucket.mem.MemoryBucket;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BBlob;
import org.smoothbuild.virtualmachine.testing.BytecodeTestContext;

public class FileContentReaderTest extends BytecodeTestContext {
  @Test
  void read_returns_file_content() throws Exception {
    var bucket = new MemoryBucket();
    var alias = alias("project");
    var filesystem = new Filesystem(map(alias, bucket));
    var fileContentReader = new FileContentReader(filesystem, exprDb());
    var path = path("my/file");
    var content = "file content";
    createFile(bucket, path, content);

    BBlob blob = fileContentReader.read(fullPath(alias, path));

    assertThat(blob).isEqualTo(bBlob(content));
  }

  @Test
  void read_caches_file_content() throws Exception {
    var bucket = new MemoryBucket();
    var alias = alias("project");
    var fileResolver = new Filesystem(map(alias, bucket));
    var fileContentReader = new FileContentReader(fileResolver, exprDb());
    var path = path("my/file");
    var content = "file content";
    createFile(bucket, path, content);

    BBlob blob = fileContentReader.read(fullPath(alias, path));
    bucket.delete(path);
    BBlob cached = fileContentReader.read(fullPath(alias, path));

    assertThat(cached).isEqualTo(blob);
  }
}
