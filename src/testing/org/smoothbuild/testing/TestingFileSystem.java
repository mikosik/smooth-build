package org.smoothbuild.testing;

import static org.smoothbuild.testing.TestingStream.assertContent;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.smoothbuild.fs.mem.MemoryFileSystem;
import org.smoothbuild.plugin.Path;

public class TestingFileSystem extends MemoryFileSystem {

  /**
   * Creates textual file that contains its path.
   */
  public void createFileContainingItsPath(Path root, Path path) throws IOException {
    createFileWithContent(fullPath(root, path), path.value());
  }

  public void createEmptyFile(Path path) throws IOException {
    createFileWithContent(path, "");
  }

  public void createFileWithContent(Path path, String content) throws IOException {
    OutputStream outputStream = createOutputStream(path);
    TestingStream.writeAndClose(outputStream, content);
  }

  public void assertFileContainsItsPath(Path root, Path path) throws IOException,
      FileNotFoundException {
    assertFileContains(fullPath(root, path), path.value());
  }

  public void assertFileContains(Path path, String content) throws IOException {
    InputStream inputStream = createInputStream(path);
    assertContent(inputStream, content);
  }

  private static Path fullPath(Path root, Path path) {
    return root.append(path);
  }
}
