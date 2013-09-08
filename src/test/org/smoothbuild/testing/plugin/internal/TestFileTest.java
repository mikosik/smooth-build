package org.smoothbuild.testing.plugin.internal;

import static org.junit.Assert.fail;
import static org.smoothbuild.plugin.api.Path.path;

import java.io.IOException;

import org.junit.Test;
import org.smoothbuild.plugin.api.Path;
import org.smoothbuild.testing.common.StreamTester;
import org.smoothbuild.testing.fs.base.TestFileSystem;

public class TestFileTest {
  Path path = path("my/file");
  TestFileSystem fileSystem = new TestFileSystem();

  TestFile testFile = new TestFile(fileSystem, path);

  @Test
  public void createContentWithFilePath() throws IOException {
    testFile.createContentWithFilePath();

    StreamTester.assertContent(fileSystem.openInputStream(path), path.value());
  }

  @Test
  public void assertContentContainsFilePathSucceeds() throws Exception {
    StreamTester.writeAndClose(fileSystem.openOutputStream(path), path.value());
    testFile.assertContentContainsFilePath();
  }

  @Test
  public void assertContentContainsFilePathFails() throws Exception {
    StreamTester.writeAndClose(fileSystem.openOutputStream(path), "other content");
    try {
      testFile.assertContentContainsFilePath();
    } catch (AssertionError e) {
      // expected
      return;
    }
    fail("exception should be thrown");
  }
}
