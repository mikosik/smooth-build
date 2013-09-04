package org.smoothbuild.fs.plugin;

import static org.smoothbuild.plugin.Path.path;

import java.io.IOException;

import org.junit.Test;
import org.smoothbuild.plugin.File;
import org.smoothbuild.plugin.Path;
import org.smoothbuild.testing.TestingFileSystem;

import com.google.common.collect.ImmutableList;

public class StoredFileSetTest {
  TestingFileSystem fileSystem = new TestingFileSystem();
  Path root = Path.path("root/dir");

  StoredFileSet storedFileSet = new StoredFileSet(fileSystem, root);

  @Test
  public void file() throws Exception {
    fileSystem.createFileContainingItsPath(root, path("abc.txt"));
    File file = storedFileSet.file(path("abc.txt"));
    StoredFileTest.assertContentHasFilePath(file);
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

    for (File file : storedFileSet) {
      StoredFileTest.assertContentHasFilePath(file);
    }
  }

  @Test(expected = IllegalArgumentException.class)
  public void fetchingNonexistentFileThrowsException() throws Exception {
    storedFileSet.file(path("nonexistent"));
  }
}
