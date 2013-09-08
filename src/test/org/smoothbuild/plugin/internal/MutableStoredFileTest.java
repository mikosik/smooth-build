package org.smoothbuild.plugin.internal;

import static org.smoothbuild.plugin.api.Path.path;
import static org.smoothbuild.testing.common.StreamTester.writeAndClose;
import static org.smoothbuild.testing.plugin.internal.TestFile.assertContentContainsFilePath;

import org.junit.Test;
import org.smoothbuild.plugin.api.Path;
import org.smoothbuild.testing.fs.base.TestFileSystem;

public class MutableStoredFileTest {
  TestFileSystem fileSystem = new TestFileSystem();
  Path filePath = path("xyz/test.txt");

  MutableStoredFile storedFile = new MutableStoredFile(fileSystem, filePath);

  @Test
  public void createOutputStream() throws Exception {
    writeAndClose(storedFile.openOutputStream(), filePath.value());
    assertContentContainsFilePath(storedFile);
  }
}
