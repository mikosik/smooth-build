package org.smoothbuild.testing;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;
import static org.smoothbuild.plugin.Path.path;
import static org.smoothbuild.testing.TestingFileContent.assertFileContent;
import static org.smoothbuild.testing.TestingFileContent.writeAndClose;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.junit.Test;
import org.smoothbuild.plugin.Path;

public class TestingFileSystemTest {
  String content = "my content";
  String pathString = "my/path/file.txt";
  Path path = path(pathString);

  TestingFileSystem fileSystem = new TestingFileSystem();

  @Test
  public void createFileContiningPath() throws Exception {
    String root = "my/root";
    fileSystem.createFileContainingPath(root, pathString);
    InputStream is = fileSystem.createInputStream(path(root + "/" + pathString));

    assertFileContent(is, pathString);
  }

  @Test
  public void createFileWithContent() throws Exception {
    fileSystem.createFileWithContent(path, content);
    InputStream is = fileSystem.createInputStream(path);

    assertFileContent(is, content);
  }

  @Test
  public void createEmptyFile() throws IOException {
    fileSystem.createEmptyFile(pathString);
    InputStream is = fileSystem.createInputStream(path);
    assertThat(is.available()).isEqualTo(0);
    is.close();
  }

  @Test
  public void assertFileContainsItsPathSucceedsWhenContentMatches() throws Exception {
    Path root = path("root/dir");
    OutputStream os = fileSystem.createOutputStream(root.append(path));
    writeAndClose(os, pathString);

    fileSystem.assertFileContainsItsPath(root.value(), pathString);
  }

  @Test
  public void assertFileContainsItsPathWhenContentDoesNotMatch() throws Exception {
    Path root = path("root/dir");
    OutputStream os = fileSystem.createOutputStream(root.append(path));
    writeAndClose(os, pathString + "abc");

    try {
      fileSystem.assertFileContainsItsPath(root.value(), pathString);
    } catch (AssertionError e) {
      // expected
      return;
    }
    fail("exception should be thrown");
  }

  @Test
  public void assertFileContainsSucceedsWhenContentMatches() throws Exception {
    OutputStream os = fileSystem.createOutputStream(path);
    writeAndClose(os, content);

    fileSystem.assertFileContains(path, content);
  }

  @Test
  public void assertFileContainsFailsWhenContentDoesNotMatch() throws Exception {
    OutputStream os = fileSystem.createOutputStream(path);
    writeAndClose(os, content);

    try {
      fileSystem.assertFileContains(path, content + "abc");
    } catch (AssertionError e) {
      // expected
      return;
    }
    fail("exception should be thrown");
  }
}
