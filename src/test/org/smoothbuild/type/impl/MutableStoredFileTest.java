package org.smoothbuild.type.impl;

import static org.smoothbuild.fs.base.Path.path;
import static org.smoothbuild.testing.common.StreamTester.writeAndClose;

import org.junit.Test;
import org.smoothbuild.fs.base.Path;
import org.smoothbuild.testing.fs.base.FakeFileSystem;
import org.smoothbuild.testing.type.impl.FileTester;
import org.smoothbuild.type.impl.MutableStoredFile;

public class MutableStoredFileTest {
  FakeFileSystem fileSystem = new FakeFileSystem();
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
