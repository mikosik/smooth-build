package org.smoothbuild.io.fs.base;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.smoothbuild.io.fs.base.Path.path;
import static org.smoothbuild.io.fs.base.RecursivePathsIterator.recursivePathsIterator;
import static org.smoothbuild.testing.common.AssertCall.assertCall;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.smoothbuild.io.fs.mem.MemoryFileSystem;

import okio.BufferedSink;
import okio.ByteString;

public class RecursivePathsIteratorTest {
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
  public void iterates_subdirectory() throws Exception {
    doTestIterable(
        "abc", new String[] {"1.txt", "2.txt", "3.txt", "def/4.txt", "def/5.txt", "ghi/6.txt"},
        "abc/def", new String[] {"4.txt", "5.txt"});
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
    try (BufferedSink sink = fileSystem.sink(path("my/file"))) {
      sink.write(ByteString.encodeUtf8("abc"));
    }
    try {
      recursivePathsIterator(fileSystem, path("my/file"));
      fail("exception should be thrown");
    } catch (IllegalArgumentException e) {
      // expected
    }
  }

  @Test
  public void throws_exception_when_dir_disappears_during_iteration() throws Exception {
    FileSystem fileSystem = new MemoryFileSystem();
    createFiles(fileSystem, "dir", "1.txt", "2.txt", "subdir/somefile");

    PathIterator iterator = recursivePathsIterator(fileSystem, path("dir"));
    iterator.next();
    fileSystem.delete(path("dir/subdir"));

    assertCall(iterator::next).throwsException(new IOException(
        "FileSystem changed when iterating tree of directory 'dir'. Cannot find 'dir/subdir'."));
  }

  private void doTestIterable(String rootDir, String[] names, String expectedRootDir,
      String[] expectedNames) throws IOException {
    FileSystem fileSystem = new MemoryFileSystem();
    createFiles(fileSystem, rootDir, names);

    PathIterator iterator = recursivePathsIterator(fileSystem, path(expectedRootDir));
    List<String> created = new ArrayList<>();
    while (iterator.hasNext()) {
      created.add(iterator.next().value());
    }
    assertThat(created, containsInAnyOrder(expectedNames));
  }

  private void createFiles(FileSystem fileSystem, String rootDir, String... names) throws
      IOException {
    for (String name : names) {
      Path path = path(rootDir).append(path(name));
      fileSystem.sink(path).close();
    }
  }
}
