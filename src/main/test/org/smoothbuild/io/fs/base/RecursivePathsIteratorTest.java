package org.smoothbuild.io.fs.base;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.jupiter.api.Assertions.fail;
import static org.smoothbuild.io.fs.base.PathS.path;
import static org.smoothbuild.io.fs.base.RecursivePathsIterator.recursivePathsIterator;
import static org.smoothbuild.testing.common.AssertCall.assertCall;
import static org.smoothbuild.util.collect.Lists.list;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.smoothbuild.io.fs.mem.MemoryFileSystem;

import okio.BufferedSink;
import okio.ByteString;

public class RecursivePathsIteratorTest {
  @Test
  public void test() throws IOException {
    doTestIterable("abc",
        list("1.txt", "2.txt", "3.txt", "def/4.txt", "def/5.txt", "ghi/6.txt"));
    doTestIterable("abc/xyz",
        list("1.txt", "2.txt", "3.txt", "def/4.txt", "def/5.txt", "ghi/6.txt"));
    doTestIterable("abc/xyz/prs",
        list("1.txt", "2.txt", "3.txt", "def/4.txt", "def/5.txt", "ghi/6.txt"));
  }

  private void doTestIterable(String rootDir, List<String> names) throws IOException {
    doTestIterable(rootDir, names, rootDir, names);
  }

  @Test
  public void iterates_subdirectory() throws Exception {
    doTestIterable(
        "abc", list("1.txt", "2.txt", "3.txt", "def/4.txt", "def/5.txt", "ghi/6.txt"),
        "abc/def", list("4.txt", "5.txt"));
  }

  @Test
  public void is_empty_when_dir_doesnt_exist() throws Exception {
    FileSystem fileSystem = new MemoryFileSystem();
    PathS path = path("my/file");
    assertThat(recursivePathsIterator(fileSystem, path).hasNext())
        .isFalse();
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
    createFiles(fileSystem, "dir", list("1.txt", "2.txt", "subdir/somefile"));

    PathIterator iterator = recursivePathsIterator(fileSystem, path("dir"));
    iterator.next();
    fileSystem.delete(path("dir/subdir"));

    assertCall(iterator::next).throwsException(new IOException(
        "FileSystem changed when iterating tree of directory 'dir'. Cannot find 'dir/subdir'."));
  }

  private void doTestIterable(String rootDir, List<String> names, String expectedRootDir,
      List<String> expectedNames) throws IOException {
    FileSystem fileSystem = new MemoryFileSystem();
    createFiles(fileSystem, rootDir, names);

    PathIterator iterator = recursivePathsIterator(fileSystem, path(expectedRootDir));
    List<String> created = new ArrayList<>();
    while (iterator.hasNext()) {
      created.add(iterator.next().toString());
    }
    assertThat(created)
        .containsExactlyElementsIn(expectedNames);
  }

  private void createFiles(FileSystem fileSystem, String rootDir, List<String> names) throws
      IOException {
    for (String name : names) {
      PathS path = path(rootDir).append(path(name));
      fileSystem.sink(path).close();
    }
  }
}
