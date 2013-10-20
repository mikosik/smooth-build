package org.smoothbuild.testing.fs.base;

import static org.junit.Assert.fail;
import static org.smoothbuild.fs.base.Path.path;
import static org.smoothbuild.testing.common.StreamTester.assertContent;
import static org.smoothbuild.testing.common.StreamTester.writeAndClose;

import java.io.IOException;
import java.io.OutputStream;

import org.junit.Test;
import org.smoothbuild.fs.base.Path;
import org.smoothbuild.testing.type.impl.FakeFile;

public class FakeFileSystemTest {
  Path root = path("my/root");
  Path path = path("my/path/file.txt");
  String content = "my content";

  FakeFileSystem fileSystem = new FakeFileSystem();

  @Test
  public void createFileContiningItsPath() throws Exception {
    FakeFile file = fileSystem.createFileContainingItsPath(path);

    assertContent(fileSystem.openInputStream(path), path.value());
    assertContent(file.openInputStream(), path.value());
  }

  @Test
  public void createFileWithContent() throws Exception {
    FakeFile file = fileSystem.createFileWithContent(path, content);

    assertContent(fileSystem.openInputStream(path), content);
    assertContent(file.openInputStream(), content);
  }

  @Test
  public void createEmptyFile() throws IOException {
    FakeFile file = fileSystem.createEmptyFile(path);

    assertContent(fileSystem.openInputStream(path), "");
    assertContent(file.openInputStream(), "");
  }

  @Test
  public void assertFileContainsItsPathSucceedsWhenContentMatches() throws Exception {
    OutputStream os = fileSystem.openOutputStream(path);
    writeAndClose(os, path.value());

    fileSystem.assertFileContainsItsPath(path);
  }

  @Test
  public void assertFileContainsItsPathWhenContentDoesNotMatch() throws Exception {
    OutputStream os = fileSystem.openOutputStream(path);
    writeAndClose(os, path.value() + "abc");

    try {
      fileSystem.assertFileContainsItsPath(path);
    } catch (AssertionError e) {
      // expected
      return;
    }
    fail("exception should be thrown");
  }

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

  @Test
  public void subFileSystem() throws Exception {
    OutputStream os = fileSystem.openOutputStream(root.append(path));
    writeAndClose(os, path.value());

    fileSystem.subFileSystem(root).assertFileContainsItsPath(path);
  }
}
