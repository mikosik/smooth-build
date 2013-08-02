package org.smoothbuild.lang.internal;

import static org.smoothbuild.lang.type.Path.path;

import java.io.IOException;

import org.junit.Test;
import org.smoothbuild.fs.mem.TestingFileSystem;
import org.smoothbuild.lang.type.FileRo;
import org.smoothbuild.lang.type.Path;

import com.google.common.collect.ImmutableList;

public class FilesRoImplTest {
  private static final String ROOT_DIR = "root/dir";

  TestingFileSystem fileSystem = new TestingFileSystem();
  Path root = Path.path(ROOT_DIR);;

  FilesRoImpl filesRoImpl = new FilesRoImpl(fileSystem, root);

  @Test
  public void fileRo() throws Exception {
    fileSystem.createFile(ROOT_DIR, "abc.txt");
    FileRo fileRo = filesRoImpl.fileRo(path("abc.txt"));
    FileRoImplTest.assertContentHasFilePath(fileRo);
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

    for (FileRo file : filesRoImpl.asIterable()) {
      FileRoImplTest.assertContentHasFilePath(file);
    }
  }
}
