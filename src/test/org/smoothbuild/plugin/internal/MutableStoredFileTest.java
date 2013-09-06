package org.smoothbuild.plugin.internal;

import static org.smoothbuild.plugin.api.Path.path;
import static org.smoothbuild.testing.TestingFile.assertContentHasFilePath;
import static org.smoothbuild.testing.TestingStream.writeAndClose;

import org.junit.Test;
import org.smoothbuild.fs.base.SubFileSystem;
import org.smoothbuild.plugin.api.Path;
import org.smoothbuild.testing.TestingFileSystem;

public class MutableStoredFileTest {
  TestingFileSystem fileSystem = new TestingFileSystem();
  Path rootDir = path("abc/efg");
  Path filePath = path("xyz/test.txt");

  MutableStoredFile storedFile = new MutableStoredFile(new SubFileSystem(fileSystem, rootDir),
      filePath);

  @Test
  public void createOutputStream() throws Exception {
    writeAndClose(storedFile.openOutputStream(), filePath.value());
    assertContentHasFilePath(storedFile);
  }
}
