package org.smoothbuild.common.bucket.base;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.jupiter.api.Assertions.fail;
import static org.smoothbuild.common.collect.List.list;
import static org.smoothbuild.commontesting.AssertCall.assertCall;

import com.google.common.truth.Truth;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import okio.BufferedSink;
import okio.ByteString;
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
    Bucket bucket = new MemoryBucket();
    var path = Path.path("my/file");
    Truth.assertThat(RecursivePathsIterator.recursivePathsIterator(bucket, path).hasNext())
        .isFalse();
  }

  @Test
  public void throws_exception_when_dir_is_a_file() throws Exception {
    Bucket bucket = new MemoryBucket();
    try (BufferedSink sink = bucket.sink(Path.path("my/file"))) {
      sink.write(ByteString.encodeUtf8("abc"));
    }
    try {
      RecursivePathsIterator.recursivePathsIterator(bucket, Path.path("my/file"));
      fail("exception should be thrown");
    } catch (IllegalArgumentException e) {
      // expected
    }
  }

  @Test
  public void throws_exception_when_dir_disappears_during_iteration() throws Exception {
    Bucket bucket = new MemoryBucket();
    createFiles(bucket, "dir", list("1.txt", "2.txt", "subdir/somefile"));

    PathIterator iterator = RecursivePathsIterator.recursivePathsIterator(bucket, Path.path("dir"));
    iterator.next();
    bucket.delete(Path.path("dir/subdir"));

    assertCall(iterator::next)
        .throwsException(new IOException(
            "Bucket changed when iterating tree of directory 'dir'. Cannot find 'dir/subdir'."));
  }

  private void doTestIterable(
      String rootDir, List<String> names, String expectedRootDir, List<String> expectedNames)
      throws IOException {
    Bucket bucket = new MemoryBucket();
    createFiles(bucket, rootDir, names);

    PathIterator iterator =
        RecursivePathsIterator.recursivePathsIterator(bucket, Path.path(expectedRootDir));
    List<String> created = new ArrayList<>();
    while (iterator.hasNext()) {
      created.add(iterator.next().toString());
    }
    assertThat(created).containsExactlyElementsIn(expectedNames);
  }

  private void createFiles(Bucket bucket, String rootDir, List<String> names) throws IOException {
    for (String name : names) {
      Path path = Path.path(rootDir).append(Path.path(name));
      bucket.sink(path).close();
    }
  }
}
