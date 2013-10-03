package org.smoothbuild.type.impl;

import static org.smoothbuild.testing.common.StreamTester.writeAndClose;
import static org.smoothbuild.type.api.Path.path;

import org.junit.Test;
import org.smoothbuild.testing.fs.base.TestFileSystem;
import org.smoothbuild.testing.type.impl.FileTester;
import org.smoothbuild.type.api.Path;
import org.smoothbuild.type.impl.MutableStoredFile;

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
