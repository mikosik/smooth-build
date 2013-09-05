package org.smoothbuild.testing;

import static org.smoothbuild.testing.TestingStream.assertContent;

import java.io.IOException;

import org.smoothbuild.fs.base.FileSystem;
import org.smoothbuild.plugin.api.File;
import org.smoothbuild.plugin.api.Path;
import org.smoothbuild.plugin.internal.MutableStoredFile;

public class TestingFile extends MutableStoredFile {
  public TestingFile(Path path) {
    this(new TestingFileSystem(), path);
  }

  public TestingFile(FileSystem fileSystem, Path path) {
    super(fileSystem, path);
  }

  public void createContentWithFilePath() throws IOException {
    TestingStream.writeAndClose(createOutputStream(), path().value());
  }

  public void assertContentContainsFilePath() throws IOException {
    TestingStream.assertContent(createInputStream(), path().value());
  }

  public static void assertContentHasFilePath(File file) throws IOException {
    assertContent(file.createInputStream(), file.path().value());
  }
}
