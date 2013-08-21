package org.smoothbuild.fs.plugin;

import static org.smoothbuild.plugin.Path.path;
import static org.smoothbuild.testing.TestingFileContent.writeAndClose;

import java.io.IOException;

import org.junit.Test;
import org.smoothbuild.plugin.File;
import org.smoothbuild.plugin.Path;
import org.smoothbuild.testing.TestingFileSystem;

import com.google.common.collect.ImmutableList;

public class FilesImplTest {
  private static final String ROOT_DIR = "root/dir";

  TestingFileSystem fileSystem = new TestingFileSystem();
  Path root = Path.path(ROOT_DIR);;

  FilesImpl filesImpl = new FilesImpl(fileSystem, root);

  @Test
  public void file() throws Exception {
    fileSystem.createFile(ROOT_DIR, "abc.txt");
    File file = filesImpl.file(path("abc.txt"));
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
      fileSystem.createFile(ROOT_DIR, name);
    }

    for (File file : filesImpl.asIterable()) {
      FileImplTest.assertContentHasFilePath(file);
    }
  }

  @Test
  public void createFile() throws Exception {
    String path = "abc/test.txt";
    createFile(path);

    FileImplTest.assertContentHasFilePath(filesImpl.file(path(path)));
  }

  private void createFile(String path) throws IOException {
    writeAndClose(filesImpl.createFile(path(path)).createOutputStream(), path);
  }
}
