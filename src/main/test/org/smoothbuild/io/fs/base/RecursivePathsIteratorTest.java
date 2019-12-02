package org.smoothbuild.io.fs.base;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.smoothbuild.io.fs.base.Path.path;
import static org.smoothbuild.io.fs.base.RecursivePathsIterator.recursivePathsIterator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.smoothbuild.io.fs.mem.MemoryFileSystem;

import okio.BufferedSink;
import okio.ByteString;

public class RecursivePathsIteratorTest {
  private final ByteString bytes = ByteString.encodeUtf8("abc");

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
      sink.write(bytes);
    }
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
      try (BufferedSink sink = fileSystem.sink(path)) {
        sink.write(bytes);
      }
    }

    PathIterator iterator = recursivePathsIterator(fileSystem, path(expectedRootDir));
    List<String> created = new ArrayList<>();
    while (iterator.hasNext()) {
      created.add(iterator.next().value());
    }
    assertThat(created, containsInAnyOrder(expectedNames));
  }
}
