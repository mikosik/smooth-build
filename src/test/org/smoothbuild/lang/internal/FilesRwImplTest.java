package org.smoothbuild.lang.internal;

import static org.smoothbuild.lang.type.Path.path;

import java.io.IOException;

import org.junit.Test;
import org.smoothbuild.fs.base.FileSystem;
import org.smoothbuild.fs.mem.InMemoryFileSystem;
import org.smoothbuild.lang.type.FileRo;
import org.smoothbuild.lang.type.Path;

import com.google.common.collect.ImmutableList;

public class FilesRwImplTest {
  private static final String ROOT_DIR = "root/dir";

  FileSystem fileSystem = new InMemoryFileSystem();
  Path root = Path.path(ROOT_DIR);
  FilesRwImpl filesRwImpl = new FilesRwImpl(fileSystem, root);

  @Test
  public void asIterable() throws IOException {
    testAsIterableFor(ImmutableList.of("abc"));
    testAsIterableFor(ImmutableList.of("abc", "def", "aaa/bbb"));
  }

  private void testAsIterableFor(ImmutableList<String> paths) throws IOException {
    for (String path : paths) {
      createFile(path);
    }

    for (FileRo file : filesRwImpl.asIterable()) {
      FileRoImplTest.assertContentHasFilePath(file);
    }
  }

  @Test
  public void createFileRw() throws Exception {
    String path = "abc/test.txt";
    createFile(path);

    FileRoImplTest.assertContentHasFilePath(filesRwImpl.fileRo(path(path)));
  }

  private void createFile(String path) throws IOException {
    FileRwImplTest.createFile(path, filesRwImpl.createFileRw(path(path)));
  }
}
