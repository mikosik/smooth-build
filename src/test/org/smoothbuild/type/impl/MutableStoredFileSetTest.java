package org.smoothbuild.type.impl;

import static org.smoothbuild.fs.base.Path.path;
import static org.smoothbuild.testing.type.impl.FileTester.createContentWithFilePath;

import java.io.IOException;

import org.junit.Test;
import org.smoothbuild.fs.base.Path;
import org.smoothbuild.testing.fs.base.TestFileSystem;
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
