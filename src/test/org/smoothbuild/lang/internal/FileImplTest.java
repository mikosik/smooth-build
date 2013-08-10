package org.smoothbuild.lang.internal;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.smoothbuild.lang.type.Path.path;
import static org.smoothbuild.testing.TestingFileContent.assertFileContent;
import static org.smoothbuild.testing.TestingFileContent.writeAndClose;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.junit.Test;
import org.smoothbuild.lang.type.File;
import org.smoothbuild.lang.type.Path;
import org.smoothbuild.testing.TestingFileSystem;

public class FileImplTest {
  private static final String ROOT_DIR = "abc/efg";
  private static final String FILE_PATH = "xyz/test.txt";

  TestingFileSystem fileSystem = new TestingFileSystem();
  Path rootDir = path(ROOT_DIR);
  Path filePath = path(FILE_PATH);

  FileImpl fileImpl = new FileImpl(fileSystem, rootDir, filePath);

  @Test
  public void testPath() throws Exception {
    assertThat(fileImpl.path()).isEqualTo(filePath);
  }

  @Test
  public void fullPath() {
    assertThat(fileImpl.fullPath()).isEqualTo(rootDir.append(filePath));
  }

  @Test
  public void createInputStream() throws Exception {
    fileSystem.createFile(ROOT_DIR, FILE_PATH);
    assertContentHasFilePath(fileImpl);
  }

  @Test
  public void createOutputStream() throws Exception {
    writeAndClose(fileImpl.createOutputStream(), FILE_PATH);
    FileImplTest.assertContentHasFilePath(fileImpl);
  }

  public static void assertContentHasFilePath(File file) throws IOException, FileNotFoundException {
    assertFileContent(file.createInputStream(), file.path().value());
  }
}
