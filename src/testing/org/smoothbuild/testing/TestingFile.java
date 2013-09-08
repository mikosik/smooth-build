package org.smoothbuild.testing;

import static org.smoothbuild.testing.common.StreamTester.assertContent;

import java.io.IOException;

import org.smoothbuild.fs.base.FileSystem;
import org.smoothbuild.plugin.api.File;
import org.smoothbuild.plugin.api.Path;
import org.smoothbuild.plugin.internal.MutableStoredFile;
import org.smoothbuild.testing.common.StreamTester;

public class TestingFile extends MutableStoredFile {
  public TestingFile(Path path) {
    this(new TestingFileSystem(), path);
  }

  public TestingFile(FileSystem fileSystem, Path path) {
    super(fileSystem, path);
  }

  public void createContentWithFilePath() throws IOException {
    StreamTester.writeAndClose(openOutputStream(), path().value());
  }

  public void assertContentContainsFilePath() throws IOException {
    StreamTester.assertContent(openInputStream(), path().value());
  }

  public static void assertContentHasFilePath(File file) throws IOException {
    assertContent(file.openInputStream(), file.path().value());
  }
}
