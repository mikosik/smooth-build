package org.smoothbuild.testing.plugin.internal;


import java.io.IOException;

import org.smoothbuild.fs.base.FileSystem;
import org.smoothbuild.plugin.api.Path;
import org.smoothbuild.plugin.internal.MutableStoredFile;
import org.smoothbuild.testing.common.StreamTester;
import org.smoothbuild.testing.fs.base.TestFileSystem;

public class TestFile extends MutableStoredFile {
  public TestFile(Path path) {
    this(new TestFileSystem(), path);
  }

  public TestFile(FileSystem fileSystem, Path path) {
    super(fileSystem, path);
  }

  public void createContentWithFilePath() throws IOException {
    StreamTester.writeAndClose(openOutputStream(), path().value());
  }

  public void assertContentContainsFilePath() throws IOException {
    FileTester.assertContentContainsFilePath(this);
  }
}
