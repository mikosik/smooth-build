package org.smoothbuild.common.filesystem.base;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.common.collect.List.list;
import static org.smoothbuild.common.filesystem.base.Path.path;
import static org.smoothbuild.common.filesystem.base.RecursivePathsIterator.recursivePathsIterator;
import static org.smoothbuild.common.testing.TestingSmallFileSystem.createFile;
import static org.smoothbuild.commontesting.AssertCall.assertCall;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.smoothbuild.common.filesystem.mem.MemoryBucket;

public class RecursivePathsIteratorTest {
  @Test
  void test() throws IOException {
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
  void iterates_subdirectory() throws Exception {
    doTestIterable(
        "abc", list("1.txt", "2.txt", "3.txt", "def/4.txt", "def/5.txt", "ghi/6.txt"),
        "abc/def", list("4.txt", "5.txt"));
  }

  @Test
  void fails_when_dir_not_exists() {
    var bucket = new MemoryBucket();
    var path = path("my/file");
    assertCall(() -> recursivePathsIterator(bucket, path))
        .throwsException(new IOException("Dir 'my/file' doesn't exist."));
  }

  @Test
  void throws_exception_when_dir_is_a_file() throws Exception {
    FileSystem<Path> bucket = new MemoryBucket();
    createFile(bucket, path("my/file"), "abc");
    assertCall(() -> recursivePathsIterator(bucket, path("my/file")))
        .throwsException(new IOException("Path 'my/file' is not a dir but a file."));
  }

  @Test
  void throws_exception_when_dir_disappears_during_iteration() throws Exception {
    var fileSystem = new MemoryBucket();
    createFiles(fileSystem, "dir", list("1.txt", "2.txt", "subdir/somefile"));

    PathIterator iterator = recursivePathsIterator(fileSystem, path("dir"));
    iterator.next();
    fileSystem.delete(path("dir/subdir"));

    assertCall(iterator::next)
        .throwsException(new IOException(
            "Bucket changed when iterating tree of directory 'dir'. Cannot find 'dir/subdir'."));
  }

  private void doTestIterable(
      String rootDir, List<String> names, String expectedRootDir, List<String> expectedNames)
      throws IOException {
    var bucket = new MemoryBucket();
    createFiles(bucket, rootDir, names);

    PathIterator iterator = recursivePathsIterator(bucket, path(expectedRootDir));
    List<String> created = new ArrayList<>();
    while (iterator.hasNext()) {
      created.add(iterator.next().toString());
    }
    assertThat(created).containsExactlyElementsIn(expectedNames);
  }

  private void createFiles(FileSystem<Path> fileSystem, String rootDir, List<String> names) throws IOException {
    for (String name : names) {
      var path = path(rootDir).append(path(name));
      createFile(fileSystem, path, "");
    }
  }
}
