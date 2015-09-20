package org.smoothbuild.io.fs.base;

import static com.google.common.collect.Iterables.isEmpty;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.smoothbuild.io.fs.base.Path.path;
import static org.smoothbuild.io.fs.base.RecursiveFilesIterable.recursiveFilesIterable;
import static org.smoothbuild.testing.io.fs.base.FileSystems.createFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.smoothbuild.io.fs.mem.MemoryFileSystem;

public class RecursiveFilesIterableTest {

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
  public void iterateOnlySubDirectory() throws Exception {
    String[] names = new String[] { "1.txt", "2.txt", "3.txt", "def/4.txt", "def/5.txt",
        "ghi/6.txt" };
    String[] expectedNames = new String[] { "4.txt", "5.txt" };

    doTestIterable("abc", names, "abc/def", expectedNames);
  }

  @Test
  public void iterateOnlySuperDirectory() throws Exception {
    String[] names = new String[] { "1.txt", "2.txt", "3.txt", "def/4.txt", "def/5.txt",
        "ghi/6.txt" };
    String[] expectedNames = new String[] { "xyz/prs/1.txt", "xyz/prs/2.txt", "xyz/prs/3.txt",
        "xyz/prs/def/4.txt", "xyz/prs/def/5.txt", "xyz/prs/ghi/6.txt" };

    doTestIterable("abc/xyz/prs", names, "abc", expectedNames);
  }

  @Test
  public void isEmptyWhenDirectoryDoesNotExist() throws Exception {
    FileSystem fileSystem = new MemoryFileSystem();
    Path path = path("my/file");

    assertTrue(isEmpty(recursiveFilesIterable(fileSystem, path)));
  }

  @Test
  public void throwsExceptionWhenDirectoryIsAFile() throws Exception {
    FileSystem fileSystem = new MemoryFileSystem();
    Path path = path("my/file");
    createFile(fileSystem, path, "content");

    try {
      recursiveFilesIterable(fileSystem, path);
      fail("exception should be thrown");
    } catch (IllegalArgumentException e) {
      // expected
    }
  }

  private void doTestIterable(String rootDir, String[] names, String expectedRootDir,
      String[] expectedNames) throws IOException {
    FileSystem fileSystem = new MemoryFileSystem();
    for (String name : names) {
      createFile(fileSystem, path(rootDir).append(path(name)), "content");
    }

    List<Path> created = new ArrayList<>();
    for (String name : expectedNames) {
      created.add(path(name));
    }

    assertThat(recursiveFilesIterable(fileSystem, path(expectedRootDir)), containsInAnyOrder(created
        .toArray(new Path[created.size()])));
  }
}
