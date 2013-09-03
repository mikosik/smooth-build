package org.smoothbuild.fs.plugin;

import static org.smoothbuild.plugin.Path.path;
import static org.smoothbuild.testing.TestingStream.writeAndClose;

import java.io.IOException;

import org.junit.Test;
import org.smoothbuild.plugin.File;
import org.smoothbuild.plugin.Path;
import org.smoothbuild.testing.TestingFileSystem;

import com.google.common.collect.ImmutableList;

public class FileSetImplTest {
  TestingFileSystem fileSystem = new TestingFileSystem();
  Path root = Path.path("root/dir");

  FileSetImpl fileSetImpl = new FileSetImpl(fileSystem, root);

  @Test
  public void file() throws Exception {
    fileSystem.createFileContainingItsPath(root, path("abc.txt"));
    File file = fileSetImpl.file(path("abc.txt"));
    FileImplTest.assertContentHasFilePath(file);
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

    for (File file : fileSetImpl.asIterable()) {
      FileImplTest.assertContentHasFilePath(file);
    }
  }

  @Test
  public void createFile() throws Exception {
    String path = "abc/test.txt";
    createFile(path);

    FileImplTest.assertContentHasFilePath(fileSetImpl.file(path(path)));
  }

  private void createFile(String path) throws IOException {
    writeAndClose(fileSetImpl.createFile(path(path)).createOutputStream(), path);
  }
}
