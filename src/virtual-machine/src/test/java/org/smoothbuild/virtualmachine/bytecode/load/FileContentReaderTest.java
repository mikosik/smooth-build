package org.smoothbuild.virtualmachine.bytecode.load;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.common.collect.Set.set;
import static org.smoothbuild.common.filesystem.base.Path.path;
import static org.smoothbuild.common.testing.TestingFileSystem.createFile;

import org.junit.jupiter.api.Test;
import org.smoothbuild.common.filesystem.mem.MemoryFileSystem;
import org.smoothbuild.virtualmachine.testing.VmTestContext;

public class FileContentReaderTest extends VmTestContext {
  @Test
  void read_returns_file_content() throws Exception {
    var alias = alias("project");
    var fileSystem = new MemoryFileSystem(set(alias));
    var fileContentReader = new FileContentReader(fileSystem, exprDb());
    var path = alias.append(path("my/file"));
    var content = "file content";
    createFile(fileSystem, path, content);

    var blob = fileContentReader.read(path);

    assertThat(blob).isEqualTo(bBlob(content));
  }

  @Test
  void read_caches_file_content() throws Exception {
    var alias = alias("project");
    var fileSystem = new MemoryFileSystem(set(alias));
    var fileContentReader = new FileContentReader(fileSystem, exprDb());
    var content = "file content";
    var path = alias.append(path("my/file"));
    createFile(fileSystem, path, content);

    var blob = fileContentReader.read(path);
    fileSystem.deleteRecursively(path);
    var cached = fileContentReader.read(path);

    assertThat(cached).isEqualTo(blob);
  }
}
