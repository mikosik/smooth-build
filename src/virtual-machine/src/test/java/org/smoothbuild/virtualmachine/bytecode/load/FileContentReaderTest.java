package org.smoothbuild.virtualmachine.bytecode.load;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.common.filesystem.base.Path.path;
import static org.smoothbuild.common.testing.TestingFileSystem.createFile;

import org.junit.jupiter.api.Test;
import org.smoothbuild.virtualmachine.dagger.VmTestContext;

public class FileContentReaderTest extends VmTestContext {
  @Test
  void read_returns_file_content() throws Exception {
    var path = provide().projectPath().append(path("my/file"));
    var content = "file content";
    createFile(provide().fileSystem(), path, content);

    var blob = provide().fileContentReader().read(path);

    assertThat(blob).isEqualTo(bBlob(content));
  }

  @Test
  void read_caches_file_content() throws Exception {
    var fileContentReader = provide().fileContentReader();
    var content = "file content";
    var path = provide().projectPath().append(path("my/file"));
    var fileSystem = provide().fileSystem();
    createFile(fileSystem, path, content);

    var blob = fileContentReader.read(path);
    fileSystem.deleteRecursively(path);
    var cached = fileContentReader.read(path);

    assertThat(cached).isEqualTo(blob);
  }
}
