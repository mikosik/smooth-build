package org.smoothbuild.testing.fs.base;

import static org.smoothbuild.testing.common.StreamTester.assertContent;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.smoothbuild.fs.base.FileSystem;
import org.smoothbuild.fs.base.SubFileSystem;
import org.smoothbuild.fs.mem.MemoryFileSystem;
import org.smoothbuild.testing.common.StreamTester;
import org.smoothbuild.testing.type.impl.TestFile;
import org.smoothbuild.type.api.Path;

public class TestFileSystem extends SubFileSystem {

  public TestFileSystem() {
    this(new MemoryFileSystem(), Path.rootPath());
  }

  public TestFileSystem(FileSystem fileSystem, Path root) {
    super(fileSystem, root);
  }

  public TestFile createFileContainingItsPath(Path path) throws IOException {
    return createFileWithContent(path, path.value());
  }

  public TestFile createEmptyFile(Path path) throws IOException {
    return createFileWithContent(path, "");
  }

  public TestFile createFileWithContent(Path path, String content) throws IOException {
    OutputStream outputStream = openOutputStream(path);
    StreamTester.writeAndClose(outputStream, content);
    return new TestFile(this, path);
  }

  public void assertFileContainsItsPath(Path path) throws IOException {
    assertFileContains(path, path.value());
  }

  public void assertFileContains(Path path, String content) throws IOException {
    InputStream inputStream = openInputStream(path);
    assertContent(inputStream, content);
  }

  public TestFileSystem subFileSystem(Path root) {
    return new TestFileSystem(this, root);
  }
}
