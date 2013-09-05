package org.smoothbuild.fs.plugin;

import static org.assertj.core.api.Assertions.assertThat;
import static org.smoothbuild.plugin.Path.path;
import static org.smoothbuild.testing.TestingFile.assertContentHasFilePath;

import java.io.IOException;

import org.junit.Test;
import org.smoothbuild.plugin.File;
import org.smoothbuild.plugin.Path;
import org.smoothbuild.testing.TestingFile;
import org.smoothbuild.testing.TestingFileSystem;

import com.google.common.collect.ImmutableList;

public class StoredFileSetTest {
  TestingFileSystem fileSystem = new TestingFileSystem();

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
    assertContentHasFilePath(file);
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
      TestingFile.assertContentHasFilePath(file);
    }
  }

  @Test(expected = IllegalArgumentException.class)
  public void fetchingNonexistentFileThrowsException() throws Exception {
    storedFileSet.file(path("nonexistent"));
  }
}
