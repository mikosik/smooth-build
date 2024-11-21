package org.smoothbuild.common.filesystem.base;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.common.collect.List.list;
import static org.smoothbuild.common.collect.Set.set;
import static org.smoothbuild.common.filesystem.base.Path.path;
import static org.smoothbuild.common.testing.TestingFileSystem.createFile;
import static org.smoothbuild.commontesting.AssertCall.assertCall;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.smoothbuild.common.filesystem.mem.MemoryFullFileSystem;

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
    var fileSystem = fileSystem();
    var path = alias().append("my/file");
    var recursivePathsIterator = new RecursivePathsIterator(fileSystem, path);
    assertCall(recursivePathsIterator::next)
        .throwsException(new IOException(
            "Cannot list files in '{alias1}/my/file'." + " Dir '{alias1}/my/file' doesn't exist."));
  }

  @Test
  void throws_exception_when_dir_is_a_file() throws Exception {
    var fileSystem = fileSystem();
    createFile(fileSystem, alias().append("my/file"), "abc");
    var recursivePathsIterator = new RecursivePathsIterator(fileSystem, alias().append("my/file"));
    assertCall(recursivePathsIterator::next)
        .throwsException(new IOException("Cannot list files in '{alias1}/my/file'. "
            + "Dir '{alias1}/my/file' doesn't exist. It is a file."));
  }

  @Test
  void throws_exception_when_dir_disappears_during_iteration() throws Exception {
    var fileSystem = fileSystem();
    createFiles(fileSystem, "dir", list("1.txt", "2.txt", "subdir/somefile"));

    FullPath dir = alias().append("dir");
    PathIterator iterator = new RecursivePathsIterator(fileSystem, dir);
    iterator.next();
    fileSystem.delete(alias().append("dir/subdir"));

    assertCall(iterator::next)
        .throwsException(
            new IOException(
                "Bucket changed when iterating tree of directory '{alias1}/dir'. Cannot find '{alias1}/dir/subdir'."));
  }

  private void doTestIterable(
      String rootDir, List<String> names, String expectedRootDir, List<String> expectedNames)
      throws IOException {
    var fileSystem = fileSystem();
    createFiles(fileSystem, rootDir, names);

    FullPath dir = alias().append(expectedRootDir);
    PathIterator iterator = new RecursivePathsIterator(fileSystem, dir);
    List<String> created = new ArrayList<>();
    while (iterator.hasNext()) {
      created.add(iterator.next().toString());
    }
    assertThat(created).containsExactlyElementsIn(expectedNames);
  }

  private void createFiles(FileSystem<FullPath> fileSystem, String rootDir, List<String> names)
      throws IOException {
    for (String name : names) {
      var path = alias().append(rootDir).append(path(name));
      createFile(fileSystem, path, "");
    }
  }

  private static MemoryFullFileSystem fileSystem() {
    return new MemoryFullFileSystem(set(alias()));
  }

  private static Alias alias() {
    return Alias.alias("alias1");
  }
}
