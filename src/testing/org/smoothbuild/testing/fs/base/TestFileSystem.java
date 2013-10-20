package org.smoothbuild.testing.fs.base;

import static org.smoothbuild.testing.common.StreamTester.assertContent;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.smoothbuild.fs.base.FileSystem;
import org.smoothbuild.fs.base.Path;
import org.smoothbuild.fs.base.SubFileSystem;
import org.smoothbuild.fs.mem.MemoryFileSystem;
import org.smoothbuild.testing.common.StreamTester;
import org.smoothbuild.testing.type.impl.FakeFile;

public class TestFileSystem extends SubFileSystem {

  public TestFileSystem() {
    this(new MemoryFileSystem(), Path.rootPath());
  }

  public TestFileSystem(FileSystem fileSystem, Path root) {
    super(fileSystem, root);
  }

  public FakeFile createFileContainingItsPath(Path path) throws IOException {
    return createFileWithContent(path, path.value());
  }

  public FakeFile createEmptyFile(Path path) throws IOException {
    return createFileWithContent(path, "");
  }

  public FakeFile createFileWithContent(Path path, String content) throws IOException {
    OutputStream outputStream = openOutputStream(path);
    StreamTester.writeAndClose(outputStream, content);
    return new FakeFile(this, path);
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
