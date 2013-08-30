package org.smoothbuild.testing;

import static org.smoothbuild.plugin.Path.path;
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
  public void createFileContainingItsPath(String root, String path) throws IOException {
    createFileWithContent(fullPath(root, path), path);
  }

  public void createFileWithContent(Path path, String content) throws IOException {
    OutputStream outputStream = createOutputStream(path);
    TestingStream.writeAndClose(outputStream, content);
  }

  public void createEmptyFile(String path) throws IOException {
    createFileWithContent(path(path), "");
  }

  public void assertFileContainsItsPath(String root, String path) throws IOException,
      FileNotFoundException {
    assertFileContains(fullPath(root, path), path);
  }

  public void assertFileContains(Path path, String content) throws IOException {
    InputStream inputStream = createInputStream(path);
    assertContent(inputStream, content);
  }

  private static Path fullPath(String root, String path) {
    return path(root).append(path(path));
  }
}
