package org.smoothbuild.fs.plugin;

import static org.assertj.core.api.Assertions.assertThat;
import static org.smoothbuild.fs.plugin.ImmutableFile.immutableFile;
import static org.smoothbuild.plugin.Path.path;
import static org.smoothbuild.testing.TestingFileContent.assertFileContent;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;
import org.smoothbuild.plugin.File;
import org.smoothbuild.plugin.Path;
import org.smoothbuild.testing.TestingFileSystem;

public class ImmutableFileTest {

  private static final String ROOT_DIR = "abc/efg";
  private static final String FILE_PATH = "xyz/test.txt";

  TestingFileSystem fileSystem = new TestingFileSystem();
  Path rootDir = path(ROOT_DIR);
  Path filePath = path(FILE_PATH);

  File file = immutableFile(new FileImpl(fileSystem, rootDir, filePath));

  @Test
  public void convertingImmutableToImmutableReturnsTheSameObject() throws Exception {
    assertThat(immutableFile(file)).isSameAs(file);
  }

  @Test
  public void testPath() throws Exception {
    assertThat(file.path()).isEqualTo(filePath);
  }

  @Test
  public void fullPath() {
    assertThat(file.fullPath()).isEqualTo(rootDir.append(filePath));
  }

  @Test
  public void createInputStream() throws Exception {
    fileSystem.createFile(ROOT_DIR, FILE_PATH);
    assertContentHasFilePath(file);
  }

  @Test
  public void createOutputStream() throws Exception {
    try {
      file.createOutputStream();
      Assert.fail("exception should be thrown");
    } catch (UnsupportedOperationException e) {
      // expected
    }
  }

  public static void assertContentHasFilePath(File file) throws IOException, FileNotFoundException {
    assertFileContent(file.createInputStream(), file.path().value());
  }
}
