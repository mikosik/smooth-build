package org.smoothbuild.testing.type.impl;

import static org.smoothbuild.fs.base.Path.path;

import org.junit.Test;
import org.smoothbuild.testing.fs.base.FakeFileSystem;
import org.smoothbuild.type.api.File;

public class FakeFileSetTest {
  FakeFileSystem fileSystem = new FakeFileSystem();
  FakeFileSet fakeFileSet = new FakeFileSet(fileSystem);

  @Test
  public void file() throws Exception {
    fileSystem.createFileContainingItsPath(path("abc.txt"));
    File file = fakeFileSet.file(path("abc.txt"));
    FileTester.assertContentContainsFilePath(file);
  }
}
