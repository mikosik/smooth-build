package org.smoothbuild.testing.type.impl;

import static org.smoothbuild.fs.base.Path.path;

import org.junit.Test;
import org.smoothbuild.testing.fs.base.TestFileSystem;
import org.smoothbuild.type.api.File;

public class TestFileSetTest {
  TestFileSystem fileSystem = new TestFileSystem();
  TestFileSet testFileSet = new TestFileSet(fileSystem);

  @Test
  public void file() throws Exception {
    fileSystem.createFileContainingItsPath(path("abc.txt"));
    File file = testFileSet.file(path("abc.txt"));
    FileTester.assertContentContainsFilePath(file);
  }
}
