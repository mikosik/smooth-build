package org.smoothbuild.fs.plugin;

import static org.smoothbuild.plugin.Path.path;
import static org.smoothbuild.testing.TestingStream.writeAndClose;

import org.junit.Test;
import org.smoothbuild.fs.base.SubFileSystem;
import org.smoothbuild.plugin.Path;
import org.smoothbuild.testing.TestingFileSystem;

public class MutableStoredFileTest {
  TestingFileSystem fileSystem = new TestingFileSystem();
  Path rootDir = path("abc/efg");
  Path filePath = path("xyz/test.txt");

  MutableStoredFile storedFile = new MutableStoredFile(new SubFileSystem(fileSystem, rootDir),
      filePath);

  @Test
  public void createOutputStream() throws Exception {
    writeAndClose(storedFile.createOutputStream(), filePath.value());
    StoredFileTest.assertContentHasFilePath(storedFile);
  }
}
