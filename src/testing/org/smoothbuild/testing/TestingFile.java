package org.smoothbuild.testing;

import static org.smoothbuild.testing.common.StreamTester.assertContent;

import java.io.IOException;

import org.smoothbuild.fs.base.FileSystem;
import org.smoothbuild.plugin.api.File;
import org.smoothbuild.plugin.api.Path;
import org.smoothbuild.plugin.internal.MutableStoredFile;
import org.smoothbuild.testing.common.StreamTester;
import org.smoothbuild.testing.fs.base.TestFileSystem;

public class TestingFile extends MutableStoredFile {
  public TestingFile(Path path) {
    this(new TestFileSystem(), path);
  }

  public TestingFile(FileSystem fileSystem, Path path) {
    super(fileSystem, path);
  }

  public void createContentWithFilePath() throws IOException {
    StreamTester.writeAndClose(openOutputStream(), path().value());
  }

  public void assertContentContainsFilePath() throws IOException {
    assertContentHasFilePath(this);
  }

  public static void assertContentHasFilePath(File file) throws IOException {
    assertContent(file.openInputStream(), file.path().value());
  }
}
