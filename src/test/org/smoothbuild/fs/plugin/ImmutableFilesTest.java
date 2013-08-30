package org.smoothbuild.fs.plugin;

import static org.assertj.core.api.Assertions.assertThat;
import static org.smoothbuild.fs.plugin.ImmutableFileList.immutableFiles;
import static org.smoothbuild.plugin.Path.path;

import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;
import org.smoothbuild.plugin.File;
import org.smoothbuild.plugin.FileList;
import org.smoothbuild.plugin.Path;
import org.smoothbuild.testing.TestingFileSystem;

import com.google.common.collect.ImmutableList;

public class ImmutableFilesTest {
  private static final String ROOT_DIR = "root/dir";

  TestingFileSystem fileSystem = new TestingFileSystem();
  Path root = Path.path(ROOT_DIR);;

  FileList fileList = immutableFiles(new FileListImpl(fileSystem, root));

  @Test
  public void convertingImmutableToImmutableReturnsTheSameObject() throws Exception {
    assertThat(immutableFiles(fileList)).isSameAs(fileList);
  }

  @Test
  public void file() throws Exception {
    fileSystem.createFileContainingItsPath(ROOT_DIR, "abc.txt");
    File file = fileList.file(path("abc.txt"));
    FileImplTest.assertContentHasFilePath(file);
  }

  @Test
  public void eachFileIsImmutable() throws Exception {
    fileSystem.createFileContainingItsPath(ROOT_DIR, "abc.txt");
    File file = fileList.file(path("abc.txt"));
    try {
      file.createOutputStream();
      Assert.fail("exception should be thrown");
    } catch (UnsupportedOperationException e) {
      // expected
    }
  }

  @Test
  public void asIterable() throws IOException {
    testAsIterableFor(ImmutableList.of("abc"));
    testAsIterableFor(ImmutableList.of("abc", "def", "aaa/bbb"));
  }

  /**
   * Creates files given as parameter with content equal to its name. Reads
   * files back asserting that content is correct.
   */
  private void testAsIterableFor(ImmutableList<String> fileNames) throws IOException {
    for (String name : fileNames) {
      fileSystem.createFileContainingItsPath(ROOT_DIR, name);
    }

    for (File file : fileList.asIterable()) {
      FileImplTest.assertContentHasFilePath(file);
    }
  }

  @Test
  public void createFileThrowsUnsupportedOperationException() throws Exception {
    try {
      fileList.createFile(path("abc/test.txt"));
      Assert.fail("exception should be thrown");
    } catch (UnsupportedOperationException e) {
      // expected
    }
  }
}
