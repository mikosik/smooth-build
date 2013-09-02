package org.smoothbuild.testing;

import static org.junit.Assert.fail;
import static org.smoothbuild.plugin.Path.path;
import static org.smoothbuild.testing.TestingStream.assertContent;
import static org.smoothbuild.testing.TestingStream.writeAndClose;

import java.io.IOException;
import java.io.OutputStream;

import org.junit.Test;
import org.smoothbuild.plugin.Path;

public class TestingFileSystemTest {
  Path root = path("my/root");
  Path path = path("my/path/file.txt");
  String content = "my content";

  TestingFileSystem fileSystem = new TestingFileSystem();

  @Test
  public void createFileContiningItsPath() throws Exception {
    fileSystem.createFileContainingItsPath(root, path);
    assertContent(fileSystem.createInputStream(root.append(path)), path.value());
  }

  @Test
  public void createFileWithContent() throws Exception {
    fileSystem.createFileWithContent(path, content);
    assertContent(fileSystem.createInputStream(path), content);
  }

  @Test
  public void createEmptyFile() throws IOException {
    fileSystem.createEmptyFile(path);
    assertContent(fileSystem.createInputStream(path), "");
  }

  @Test
  public void assertFileContainsItsPathSucceedsWhenContentMatches() throws Exception {
    OutputStream os = fileSystem.createOutputStream(root.append(path));
    writeAndClose(os, path.value());

    fileSystem.assertFileContainsItsPath(root, path);
  }

  @Test
  public void assertFileContainsItsPathWhenContentDoesNotMatch() throws Exception {
    OutputStream os = fileSystem.createOutputStream(root.append(path));
    writeAndClose(os, path.value() + "abc");

    try {
      fileSystem.assertFileContainsItsPath(root, path);
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
