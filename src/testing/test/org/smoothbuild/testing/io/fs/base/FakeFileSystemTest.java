package org.smoothbuild.testing.io.fs.base;

import static org.junit.Assert.fail;
import static org.smoothbuild.io.fs.base.Path.path;
import static org.smoothbuild.testing.common.StreamTester.writeAndClose;

import java.io.OutputStream;

import org.junit.Test;
import org.smoothbuild.io.fs.base.Path;

public class FakeFileSystemTest {
  Path root = path("my/root");
  Path path = path("my/path/file.txt");
  String content = "my content";

  FakeFileSystem fileSystem = new FakeFileSystem();

  @Test
  public void assertFileContainsSucceedsWhenContentMatches() throws Exception {
    OutputStream os = fileSystem.openOutputStream(path);
    writeAndClose(os, content);

    fileSystem.assertFileContains(path, content);
  }

  @Test
  public void assertFileContainsFailsWhenContentDoesNotMatch() throws Exception {
    OutputStream os = fileSystem.openOutputStream(path);
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
