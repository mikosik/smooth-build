package org.smoothbuild.testing.type.impl;

import static org.junit.Assert.fail;
import static org.smoothbuild.fs.base.Path.path;

import java.io.IOException;

import org.junit.Test;
import org.smoothbuild.fs.base.Path;
import org.smoothbuild.testing.common.StreamTester;
import org.smoothbuild.testing.fs.base.FakeFileSystem;
import org.smoothbuild.testing.type.impl.FakeFile;

public class FakeFileTest {
  Path path = path("my/file");
  FakeFileSystem fileSystem = new FakeFileSystem();

  FakeFile fakeFile = new FakeFile(fileSystem, path);

  @Test
  public void createContentWithFilePath() throws IOException {
    fakeFile.createContentWithFilePath();

    StreamTester.assertContent(fileSystem.openInputStream(path), path.value());
  }

  @Test
  public void createContent() throws IOException {
    String content = "some content";
    fakeFile.createContent(content);

    StreamTester.assertContent(fileSystem.openInputStream(path), content);
  }

  @Test
  public void assertContentContainsFilePathSucceeds() throws Exception {
    StreamTester.writeAndClose(fileSystem.openOutputStream(path), path.value());
    fakeFile.assertContentContainsFilePath();
  }

  @Test
  public void assertContentContainsFilePathFails() throws Exception {
    StreamTester.writeAndClose(fileSystem.openOutputStream(path), "other content");
    try {
      fakeFile.assertContentContainsFilePath();
    } catch (AssertionError e) {
      // expected
      return;
    }
    fail("exception should be thrown");
  }

  @Test
  public void assertContentContainsSucceeds() throws Exception {
    String content = "content";
    StreamTester.writeAndClose(fileSystem.openOutputStream(path), content);
    fakeFile.assertContentContains(content);
  }

  @Test
  public void assertContentContainsFails() throws Exception {
    String content = "content";
    StreamTester.writeAndClose(fileSystem.openOutputStream(path), content);
    try {
      fakeFile.assertContentContains(content + "suffix");
    } catch (AssertionError e) {
      // expected
      return;
    }
    fail("exception should be thrown");
  }
}
