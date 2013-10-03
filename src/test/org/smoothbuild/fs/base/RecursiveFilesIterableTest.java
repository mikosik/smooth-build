package org.smoothbuild.fs.base;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;
import static org.smoothbuild.fs.base.Path.path;
import static org.smoothbuild.fs.base.RecursiveFilesIterable.recursiveFilesIterable;

import java.io.IOException;
import java.util.List;

import org.junit.Test;
import org.smoothbuild.testing.fs.base.TestFileSystem;

import com.google.common.collect.Lists;

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
    TestFileSystem fileSystem = new TestFileSystem();
    Path path = path("my/file");

    assertThat(recursiveFilesIterable(fileSystem, path)).isEmpty();
  }

  @Test
  public void throwsExceptionWhenDirectoryIsAFile() throws Exception {
    TestFileSystem fileSystem = new TestFileSystem();
    Path path = path("my/file");
    fileSystem.createEmptyFile(path);

    try {
      recursiveFilesIterable(fileSystem, path);
      fail("exception should be thrown");
    } catch (IllegalArgumentException e) {
      // expected
    }
  }

  private void doTestIterable(String rootDir, String[] names, String expectedRootDir,
      String[] expectedNames) throws IOException {
    TestFileSystem fileSystem = new TestFileSystem();
    for (String name : names) {
      fileSystem.createEmptyFile(path(rootDir).append(path(name)));
    }

    List<Path> created = Lists.newArrayList();
    for (String name : expectedNames) {
      created.add(path(name));
    }

    assertThat(recursiveFilesIterable(fileSystem, path(expectedRootDir))).containsOnly(
        created.toArray(new Path[] {}));
  }
}
