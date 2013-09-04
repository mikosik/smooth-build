package org.smoothbuild.fs.plugin;

import static org.assertj.core.api.Assertions.assertThat;
import static org.smoothbuild.fs.plugin.ImmutableFileSet.immutableFiles;
import static org.smoothbuild.plugin.Path.path;

import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;
import org.smoothbuild.plugin.File;
import org.smoothbuild.plugin.FileSet;
import org.smoothbuild.plugin.Path;
import org.smoothbuild.testing.TestingFileSystem;

import com.google.common.collect.ImmutableList;

public class ImmutableFileSetTest {
  TestingFileSystem fileSystem = new TestingFileSystem();
  Path root = Path.path("root/dir");
  Path filePath = path("abc.txt");

  FileSet fileSet = immutableFiles(new StoredFileSet(fileSystem, root));

  @Test
  public void convertingImmutableToImmutableReturnsTheSameObject() throws Exception {
    assertThat(immutableFiles(fileSet)).isSameAs(fileSet);
  }

  @Test
  public void file() throws Exception {
    fileSystem.createFileContainingItsPath(root, filePath);
    File file = fileSet.file(filePath);
    FileImplTest.assertContentHasFilePath(file);
  }

  @Test
  public void eachFileIsImmutable() throws Exception {
    fileSystem.createFileContainingItsPath(root, filePath);
    File file = fileSet.file(path("abc.txt"));
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
      fileSystem.createFileContainingItsPath(root, path(name));
    }

    for (File file : fileSet) {
      FileImplTest.assertContentHasFilePath(file);
    }
  }

  @Test
  public void createFileThrowsUnsupportedOperationException() throws Exception {
    try {
      fileSet.createFile(path("abc/test.txt"));
      Assert.fail("exception should be thrown");
    } catch (UnsupportedOperationException e) {
      // expected
    }
  }
}
