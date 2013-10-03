package org.smoothbuild.type.impl;

import static org.smoothbuild.testing.type.impl.FileTester.createContentWithFilePath;
import static org.smoothbuild.type.api.Path.path;

import java.io.IOException;

import org.junit.Test;
import org.smoothbuild.testing.fs.base.TestFileSystem;
import org.smoothbuild.type.api.Path;
import org.smoothbuild.type.impl.MutableStoredFileSet;

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
