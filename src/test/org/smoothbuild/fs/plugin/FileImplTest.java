package org.smoothbuild.fs.plugin;

import static org.assertj.core.api.Assertions.assertThat;
import static org.smoothbuild.plugin.Path.path;
import static org.smoothbuild.testing.TestingStream.assertContent;
import static org.smoothbuild.testing.TestingStream.writeAndClose;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.junit.Test;
import org.smoothbuild.plugin.File;
import org.smoothbuild.plugin.Path;
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
    fileSystem.createFileContainingPath(ROOT_DIR, FILE_PATH);
    assertContentHasFilePath(fileImpl);
  }

  @Test
  public void createOutputStream() throws Exception {
    writeAndClose(fileImpl.createOutputStream(), FILE_PATH);
    FileImplTest.assertContentHasFilePath(fileImpl);
  }

  public static void assertContentHasFilePath(File file) throws IOException, FileNotFoundException {
    assertContent(file.createInputStream(), file.path().value());
  }
}
