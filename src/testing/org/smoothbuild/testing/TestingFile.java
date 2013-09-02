package org.smoothbuild.testing;

import java.io.IOException;

import org.smoothbuild.fs.base.FileSystem;
import org.smoothbuild.fs.plugin.FileImpl;
import org.smoothbuild.plugin.Path;

public class TestingFile extends FileImpl {
  public TestingFile(FileSystem fileSystem, Path root, Path path) {
    super(fileSystem, root, path);
  }

  public void createContentWithFilePath() throws IOException {
    TestingStream.writeAndClose(createOutputStream(), path().value());
  }

  public void assertContentContainsFilePath() throws IOException {
    TestingStream.assertContent(createInputStream(), path().value());
  }
}
