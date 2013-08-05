package org.smoothbuild.fs.base;

import static org.fest.assertions.api.Assertions.assertThat;

import java.io.IOException;

import org.junit.Test;
import org.smoothbuild.testing.TestingFileSystem;

public class RecursiveFilesIterableTest {

  @Test
  public void test() throws IOException {
    doTestIterable("abc", "1.txt", "2.txt", "3.txt", "def/4.txt", "def/5.txt", "ghi/6.txt");
    doTestIterable("abc/xyz", "1.txt", "2.txt", "3.txt", "def/4.txt", "def/5.txt", "ghi/6.txt");
    doTestIterable("abc/xyz/prs", "1.txt", "2.txt", "3.txt", "def/4.txt", "def/5.txt", "ghi/6.txt");
  }

  @Test
  public void iterateOnlySubDirectory() throws Exception {
    String[] names = new String[] { "1.txt", "2.txt", "3.txt", "def/4.txt", "def/5.txt",
        "ghi/6.txt" };
    String[] expectedNames = new String[] { "def/4.txt", "def/5.txt" };

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

  private void doTestIterable(String rootDir, String... names) throws IOException {
    doTestIterable(rootDir, names, rootDir, names);
  }

  private void doTestIterable(String rootDir, String[] names, String expectedRootDir,
      String[] expectedNames) throws IOException {
    TestingFileSystem fileSystem = new TestingFileSystem();
    for (String name : names) {
      fileSystem.createFile(rootDir, name);
    }

    assertThat(new RecursiveFilesIterable(fileSystem, rootDir)).containsOnly(names);
  }
}
