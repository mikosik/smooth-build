package org.smoothbuild.lang.internal;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.smoothbuild.lang.type.Path.path;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

import org.junit.Test;
import org.smoothbuild.fs.mem.TestingFileSystem;
import org.smoothbuild.lang.type.FileRo;
import org.smoothbuild.lang.type.Path;

import com.google.common.io.LineReader;

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
    try (InputStreamReader readable = new InputStreamReader(file.createInputStream());) {
      LineReader reader = new LineReader(readable);
      assertThat(reader.readLine()).isEqualTo(file.path().value());
    }
  }
}
