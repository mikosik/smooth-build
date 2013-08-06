package org.smoothbuild.lang.internal;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.smoothbuild.lang.type.Path.path;
import static org.smoothbuild.testing.TestingFileContent.assertFileContent;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.junit.Test;
import org.smoothbuild.lang.type.FileRo;
import org.smoothbuild.lang.type.Path;
import org.smoothbuild.testing.TestingFileSystem;

public class FileRoImplTest {
  private static final String ROOT_DIR = "abc/efg";
  private static final String FILE_PATH = "xyz/test.txt";

  TestingFileSystem fileSystem = new TestingFileSystem();
  Path rootDir = path(ROOT_DIR);
  Path filePath = path(FILE_PATH);

  FileRoImpl fileRoImpl = new FileRoImpl(fileSystem, rootDir, filePath);

  @Test
  public void fileSystem() throws Exception {
    assertThat(fileRoImpl.fileSystem()).isEqualTo(fileSystem);
  }

  @Test
  public void testPath() throws Exception {
    assertThat(fileRoImpl.path()).isEqualTo(filePath);
  }

  @Test
  public void fullPath() {
    assertThat(fileRoImpl.fullPath()).isEqualTo(rootDir.append(filePath));
  }

  @Test
  public void createInputStream() throws Exception {
    fileSystem.createFile(ROOT_DIR, FILE_PATH);
    assertContentHasFilePath(fileRoImpl);
  }

  public static void assertContentHasFilePath(FileRo file) throws IOException,
      FileNotFoundException {
    assertFileContent(file.createInputStream(), file.path().value());
  }
}
