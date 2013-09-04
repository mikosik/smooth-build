package org.smoothbuild.testing;

import java.io.IOException;

import org.smoothbuild.fs.base.FileSystem;
import org.smoothbuild.fs.plugin.MutableStoredFile;
import org.smoothbuild.plugin.Path;

public class TestingFile extends MutableStoredFile {
  public TestingFile(FileSystem fileSystem, Path path) {
    super(fileSystem, path);
  }

  public void createContentWithFilePath() throws IOException {
    TestingStream.writeAndClose(createOutputStream(), path().value());
  }

  public void assertContentContainsFilePath() throws IOException {
    TestingStream.assertContent(createInputStream(), path().value());
  }
}
