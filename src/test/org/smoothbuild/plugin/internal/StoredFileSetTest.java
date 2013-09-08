package org.smoothbuild.plugin.internal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.smoothbuild.plugin.api.Path.path;
import static org.smoothbuild.testing.TestingFile.assertContentContainsFilePath;

import java.io.IOException;

import org.junit.Test;
import org.smoothbuild.plugin.api.File;
import org.smoothbuild.plugin.api.Path;
import org.smoothbuild.testing.TestingFile;
import org.smoothbuild.testing.fs.base.TestFileSystem;

import com.google.common.collect.ImmutableList;

public class StoredFileSetTest {
  TestFileSystem fileSystem = new TestFileSystem();

  StoredFileSet storedFileSet = new StoredFileSet(fileSystem);

  @Test
  public void containsReturnsTrueForExistingFile() throws Exception {
    Path path = path("my/file");
    fileSystem.createEmptyFile(path);
    assertThat(storedFileSet.contains(path)).isTrue();
  }

  @Test
  public void containsReturnsFalseForNonexistentFile() throws Exception {
    Path path = path("my/file");
    assertThat(storedFileSet.contains(path)).isFalse();
  }

  @Test
  public void file() throws Exception {
    fileSystem.createFileContainingItsPath(path("abc.txt"));
    File file = storedFileSet.file(path("abc.txt"));
    assertContentContainsFilePath(file);
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
      fileSystem.createFileContainingItsPath(path(name));
    }

    for (File file : storedFileSet) {
      TestingFile.assertContentContainsFilePath(file);
    }
  }

  @Test(expected = IllegalArgumentException.class)
  public void fetchingNonexistentFileThrowsException() throws Exception {
    storedFileSet.file(path("nonexistent"));
  }
}
