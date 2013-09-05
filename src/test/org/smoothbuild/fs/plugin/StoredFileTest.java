package org.smoothbuild.fs.plugin;

import static org.assertj.core.api.Assertions.assertThat;
import static org.smoothbuild.plugin.Path.path;
import static org.smoothbuild.testing.TestingFile.assertContentHasFilePath;

import org.junit.Test;
import org.smoothbuild.fs.base.SubFileSystem;
import org.smoothbuild.plugin.Path;
import org.smoothbuild.testing.TestingFileSystem;

public class StoredFileTest {
  TestingFileSystem fileSystem = new TestingFileSystem();
  Path rootDir = path("abc/efg");
  Path filePath = path("xyz/test.txt");

  StoredFile storedFile = new StoredFile(new SubFileSystem(fileSystem, rootDir), filePath);

  @Test
  public void testPath() throws Exception {
    assertThat(storedFile.path()).isEqualTo(filePath);
  }

  @Test
  public void fullPath() {
    assertThat(storedFile.fullPath()).isEqualTo(rootDir.append(filePath));
  }

  @Test
  public void createInputStream() throws Exception {
    fileSystem.createFileContainingItsPath(rootDir, filePath);
    assertContentHasFilePath(storedFile);
  }

}
