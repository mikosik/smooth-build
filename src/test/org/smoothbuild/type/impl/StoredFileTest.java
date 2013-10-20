package org.smoothbuild.type.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.smoothbuild.fs.base.Path.path;

import org.junit.Test;
import org.smoothbuild.fs.base.Path;
import org.smoothbuild.testing.fs.base.FakeFileSystem;
import org.smoothbuild.testing.type.impl.FileTester;
import org.smoothbuild.type.impl.StoredFile;

public class StoredFileTest {
  FakeFileSystem fileSystem = new FakeFileSystem();
  Path filePath = path("xyz/test.txt");

  StoredFile storedFile = new StoredFile(fileSystem, filePath);

  @Test
  public void testPath() throws Exception {
    assertThat(storedFile.path()).isEqualTo(filePath);
  }

  @Test
  public void fileSystem() throws Exception {
    assertThat(storedFile.fileSystem()).isSameAs(fileSystem);
  }

  @Test
  public void createInputStream() throws Exception {
    fileSystem.createFileContainingItsPath(filePath);
    FileTester.assertContentContainsFilePath(storedFile);
  }

  @Test
  public void testToString() throws Exception {
    assertThat(storedFile.toString()).isEqualTo("StoredFile(" + filePath.toString() + ")");
  }

}
