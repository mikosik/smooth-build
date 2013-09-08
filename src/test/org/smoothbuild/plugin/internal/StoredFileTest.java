package org.smoothbuild.plugin.internal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.smoothbuild.plugin.api.Path.path;
import static org.smoothbuild.testing.TestingFile.assertContentHasFilePath;

import org.junit.Test;
import org.smoothbuild.plugin.api.Path;
import org.smoothbuild.testing.TestingFileSystem;

public class StoredFileTest {
  TestingFileSystem fileSystem = new TestingFileSystem();
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
    assertContentHasFilePath(storedFile);
  }

}
