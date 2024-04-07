package org.smoothbuild.common.bucket.base;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.jupiter.api.Assertions.fail;
import static org.smoothbuild.common.bucket.base.Path.path;
import static org.smoothbuild.common.bucket.base.RecursivePathsIterator.recursivePathsIterator;
import static org.smoothbuild.common.collect.List.list;
import static org.smoothbuild.common.testing.TestingBucket.createFile;
import static org.smoothbuild.commontesting.AssertCall.assertCall;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.smoothbuild.common.bucket.mem.MemoryBucket;

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
    var bucket = new MemoryBucket();
    var path = path("my/file");
    assertThat(recursivePathsIterator(bucket, path).hasNext()).isFalse();
  }

  @Test
  public void throws_exception_when_dir_is_a_file() throws Exception {
    Bucket bucket = new MemoryBucket();
    createFile(bucket, path("my/file"), "abc");
    try {
      recursivePathsIterator(bucket, path("my/file"));
      fail("exception should be thrown");
    } catch (IllegalArgumentException e) {
      // expected
    }
  }

  @Test
  public void throws_exception_when_dir_disappears_during_iteration() throws Exception {
    var bucket = new MemoryBucket();
    createFiles(bucket, "dir", list("1.txt", "2.txt", "subdir/somefile"));

    PathIterator iterator = recursivePathsIterator(bucket, path("dir"));
    iterator.next();
    bucket.delete(path("dir/subdir"));

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

  private void createFiles(Bucket bucket, String rootDir, List<String> names) throws IOException {
    for (String name : names) {
      var path = path(rootDir).append(path(name));
      createFile(bucket, path, "");
    }
  }
}
