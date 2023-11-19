package org.smoothbuild.common.filesystem.base;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.jupiter.api.Assertions.fail;
import static org.smoothbuild.common.collect.Lists.list;
import static org.smoothbuild.testing.common.AssertCall.assertCall;

import com.google.common.truth.Truth;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import okio.BufferedSink;
import okio.ByteString;
import org.junit.jupiter.api.Test;
import org.smoothbuild.common.filesystem.mem.MemoryFileSystem;

public class RecursivePathsIteratorTest {
  @Test
  public void test() throws IOException {
    doTestIterable("abc", list("1.txt", "2.txt", "3.txt", "def/4.txt", "def/5.txt", "ghi/6.txt"));
    doTestIterable(
        "abc/xyz", list("1.txt", "2.txt", "3.txt", "def/4.txt", "def/5.txt", "ghi/6.txt"));
    doTestIterable(
        "abc/xyz/prs", list("1.txt", "2.txt", "3.txt", "def/4.txt", "def/5.txt", "ghi/6.txt"));
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
    var path = PathS.path("my/file");
    Truth.assertThat(
            RecursivePathsIterator.recursivePathsIterator(fileSystem, path).hasNext())
        .isFalse();
  }

  @Test
  public void throws_exception_when_dir_is_a_file() throws Exception {
    FileSystem fileSystem = new MemoryFileSystem();
    try (BufferedSink sink = fileSystem.sink(PathS.path("my/file"))) {
      sink.write(ByteString.encodeUtf8("abc"));
    }
    try {
      RecursivePathsIterator.recursivePathsIterator(fileSystem, PathS.path("my/file"));
      fail("exception should be thrown");
    } catch (IllegalArgumentException e) {
      // expected
    }
  }

  @Test
  public void throws_exception_when_dir_disappears_during_iteration() throws Exception {
    FileSystem fileSystem = new MemoryFileSystem();
    createFiles(fileSystem, "dir", list("1.txt", "2.txt", "subdir/somefile"));

    PathIterator iterator =
        RecursivePathsIterator.recursivePathsIterator(fileSystem, PathS.path("dir"));
    iterator.next();
    fileSystem.delete(PathS.path("dir/subdir"));

    assertCall(iterator::next)
        .throwsException(
            new IOException(
                "FileSystem changed when iterating tree of directory 'dir'. Cannot find 'dir/subdir'."));
  }

  private void doTestIterable(
      String rootDir, List<String> names, String expectedRootDir, List<String> expectedNames)
      throws IOException {
    FileSystem fileSystem = new MemoryFileSystem();
    createFiles(fileSystem, rootDir, names);

    PathIterator iterator =
        RecursivePathsIterator.recursivePathsIterator(fileSystem, PathS.path(expectedRootDir));
    List<String> created = new ArrayList<>();
    while (iterator.hasNext()) {
      created.add(iterator.next().toString());
    }
    assertThat(created).containsExactlyElementsIn(expectedNames);
  }

  private void createFiles(FileSystem fileSystem, String rootDir, List<String> names)
      throws IOException {
    for (String name : names) {
      PathS path = PathS.path(rootDir).append(PathS.path(name));
      fileSystem.sink(path).close();
    }
  }
}
