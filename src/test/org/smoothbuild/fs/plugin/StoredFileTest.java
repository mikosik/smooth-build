package org.smoothbuild.fs.plugin;

import static org.assertj.core.api.Assertions.assertThat;
import static org.smoothbuild.plugin.Path.path;
import static org.smoothbuild.testing.TestingStream.assertContent;
import static org.smoothbuild.testing.TestingStream.writeAndClose;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.junit.Test;
import org.smoothbuild.fs.base.SubFileSystem;
import org.smoothbuild.plugin.File;
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

  @Test
  public void createOutputStream() throws Exception {
    writeAndClose(storedFile.createOutputStream(), filePath.value());
    StoredFileTest.assertContentHasFilePath(storedFile);
  }

  public static void assertContentHasFilePath(File file) throws IOException, FileNotFoundException {
    assertContent(file.createInputStream(), file.path().value());
  }
}
