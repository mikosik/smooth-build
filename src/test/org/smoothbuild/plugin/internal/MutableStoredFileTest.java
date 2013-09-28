package org.smoothbuild.plugin.internal;

import static org.smoothbuild.plugin.api.Path.path;
import static org.smoothbuild.testing.common.StreamTester.writeAndClose;

import org.junit.Test;
import org.smoothbuild.plugin.api.Path;
import org.smoothbuild.testing.fs.base.TestFileSystem;
import org.smoothbuild.testing.plugin.internal.FileTester;

public class MutableStoredFileTest {
  TestFileSystem fileSystem = new TestFileSystem();
  Path filePath = path("xyz/test.txt");

  MutableStoredFile file = new MutableStoredFile(fileSystem, filePath);

  @Test
  public void createOutputStream() throws Exception {
    writeAndClose(file.openOutputStream(), filePath.value());
    FileTester.assertContentContainsFilePath(file);
  }

  @Test
  public void setContent() throws Exception {
    String otherContent = "otherContent";
    MutableStoredFile otherFile = new MutableStoredFile(fileSystem, path("otherFile"));
    FileTester.createContent(otherFile, otherContent);

    file.setContent(otherFile);
    FileTester.assertContentContains(file, otherContent);
  }
}
