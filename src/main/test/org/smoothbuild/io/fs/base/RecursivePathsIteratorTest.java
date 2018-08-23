package org.smoothbuild.io.fs.base;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.smoothbuild.io.fs.base.Path.path;
import static org.smoothbuild.io.fs.base.RecursivePathsIterator.recursivePathsIterator;
import static org.smoothbuild.util.Streams.writeAndClose;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.smoothbuild.io.fs.mem.MemoryFileSystem;

public class RecursivePathsIteratorTest {
  private final byte[] bytes = new byte[] { 1, 2, 3 };

  @Test
  public void test() throws IOException {
    doTestIterable("abc", "1.txt", "2.txt", "3.txt", "def/4.txt", "def/5.txt", "ghi/6.txt");
    doTestIterable("abc/xyz", "1.txt", "2.txt", "3.txt", "def/4.txt", "def/5.txt", "ghi/6.txt");
    doTestIterable("abc/xyz/prs", "1.txt", "2.txt", "3.txt", "def/4.txt", "def/5.txt", "ghi/6.txt");
  }

  private void doTestIterable(String rootDir, String... names) throws IOException {
    doTestIterable(rootDir, names, rootDir, names);
  }

  @Test
  public void iterate_only_sub_dir() throws Exception {
    String[] names = new String[] { "1.txt", "2.txt", "3.txt", "def/4.txt", "def/5.txt",
        "ghi/6.txt" };
    String[] expectedNames = new String[] { "4.txt", "5.txt" };

    doTestIterable("abc", names, "abc/def", expectedNames);
  }

  @Test
  public void iterate_only_super_dir() throws Exception {
    String[] names = new String[] { "1.txt", "2.txt", "3.txt", "def/4.txt", "def/5.txt",
        "ghi/6.txt" };
    String[] expectedNames = new String[] { "xyz/prs/1.txt", "xyz/prs/2.txt", "xyz/prs/3.txt",
        "xyz/prs/def/4.txt", "xyz/prs/def/5.txt", "xyz/prs/ghi/6.txt" };

    doTestIterable("abc/xyz/prs", names, "abc", expectedNames);
  }

  @Test
  public void is_empty_when_dir_doesnt_exist() throws Exception {
    FileSystem fileSystem = new MemoryFileSystem();
    Path path = path("my/file");
    assertFalse(recursivePathsIterator(fileSystem, path).hasNext());
  }

  @Test
  public void throws_exception_when_dir_is_a_file() throws Exception {
    FileSystem fileSystem = new MemoryFileSystem();
    writeAndClose(fileSystem.sink(path("my/file")).outputStream(), bytes);
    try {
      recursivePathsIterator(fileSystem, path("my/file"));
      fail("exception should be thrown");
    } catch (IllegalArgumentException e) {
      // expected
    }
  }

  private void doTestIterable(String rootDir, String[] names, String expectedRootDir,
      String[] expectedNames) throws IOException {
    FileSystem fileSystem = new MemoryFileSystem();
    for (String name : names) {
      Path path = path(rootDir).append(path(name));
      writeAndClose(fileSystem.sink(path).outputStream(), bytes);
    }

    PathIterator iterator = recursivePathsIterator(fileSystem, path(expectedRootDir));
    List<String> created = new ArrayList<>();
    while (iterator.hasNext()) {
      created.add(iterator.next().value());
    }
    assertThat(created, containsInAnyOrder(expectedNames));
  }
}
