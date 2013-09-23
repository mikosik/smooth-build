package org.smoothbuild.testing.plugin.internal;

import java.io.IOException;

import org.smoothbuild.fs.base.FileSystem;
import org.smoothbuild.plugin.api.Path;
import org.smoothbuild.plugin.internal.MutableStoredFile;
import org.smoothbuild.testing.fs.base.TestFileSystem;

public class TestFile extends MutableStoredFile {
  public TestFile(Path path) {
    this(new TestFileSystem(), path);
  }

  public TestFile(FileSystem fileSystem, Path path) {
    super(fileSystem, path);
  }

  public void createContentWithFilePath() throws IOException {
    FileTester.createContentWithFilePath(this);
  }

  public void createContent(String content) throws IOException {
    FileTester.createContent(this, content);
  }

  public void assertContentContainsFilePath() throws IOException {
    FileTester.assertContentContainsFilePath(this);
  }

  public void assertContentContains(String content) throws IOException {
    FileTester.assertContentContains(this, content);
  }
}
