package org.smoothbuild.testing.io.fs.base;

import static org.smoothbuild.testing.common.StreamTester.assertContent;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.smoothbuild.io.fs.base.FileSystem;
import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.io.fs.base.SubFileSystem;
import org.smoothbuild.io.fs.mem.MemoryFileSystem;
import org.smoothbuild.testing.common.StreamTester;

public class FakeFileSystem extends SubFileSystem {

  public FakeFileSystem() {
    this(new MemoryFileSystem(), Path.rootPath());
  }

  public FakeFileSystem(FileSystem fileSystem, Path root) {
    super(fileSystem, root);
  }

  public void createFileContainingItsPath(Path path) throws IOException {
    createFile(path, path.value());
  }

  public void createFileContainingItsPath(Path root, Path path) throws IOException {
    createFile(root.append(path), path.value());
  }

  public void createFile(Path path, String content) throws IOException {
    OutputStream outputStream = openOutputStream(path);
    StreamTester.writeAndClose(outputStream, content);
  }

  public void assertFileContainsItsPath(Path path) throws IOException {
    assertFileContains(path, path.value());
  }

  public void assertFileContainsItsPath(Path root, Path path) throws IOException {
    assertFileContains(root.append(path), path.value());
  }

  public void assertFileContains(Path path, String content) throws IOException {
    InputStream inputStream = openInputStream(path);
    assertContent(inputStream, content);
  }
}
