package org.smoothbuild.testing;

import static org.smoothbuild.testing.common.StreamTester.assertContent;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.smoothbuild.fs.base.FileSystem;
import org.smoothbuild.fs.base.SubFileSystem;
import org.smoothbuild.fs.mem.MemoryFileSystem;
import org.smoothbuild.plugin.api.Path;
import org.smoothbuild.testing.common.StreamTester;

public class TestingFileSystem extends SubFileSystem {

  public TestingFileSystem() {
    this(new MemoryFileSystem(), Path.rootPath());
  }

  public TestingFileSystem(FileSystem fileSystem, Path root) {
    super(fileSystem, root);
  }

  public void createFileContainingItsPath(Path path) throws IOException {
    createFileWithContent(path, path.value());
  }

  public void createEmptyFile(Path path) throws IOException {
    createFileWithContent(path, "");
  }

  public void createFileWithContent(Path path, String content) throws IOException {
    OutputStream outputStream = openOutputStream(path);
    StreamTester.writeAndClose(outputStream, content);
  }

  public void assertFileContainsItsPath(Path path) throws IOException {
    assertFileContains(path, path.value());
  }

  public void assertFileContains(Path path, String content) throws IOException {
    InputStream inputStream = openInputStream(path);
    assertContent(inputStream, content);
  }

  public TestingFileSystem subFileSystem(Path root) {
    return new TestingFileSystem(this, root);
  }
}
