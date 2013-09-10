package org.smoothbuild.plugin.internal;

import static org.smoothbuild.plugin.api.Path.path;
import static org.smoothbuild.testing.plugin.internal.FileTester.createContentWithFilePath;

import java.io.IOException;

import org.junit.Test;
import org.smoothbuild.plugin.api.Path;
import org.smoothbuild.testing.fs.base.TestFileSystem;

public class MutableStoredFileSetTest {
  TestFileSystem fileSystem = new TestFileSystem();
  MutableStoredFileSet fileSet = new MutableStoredFileSet(fileSystem);

  @Test
  public void createFile() throws IOException {
    Path path = path("my/file");
    createContentWithFilePath(fileSet.createFile(path));
    fileSystem.assertFileContainsItsPath(path);
  }
}
