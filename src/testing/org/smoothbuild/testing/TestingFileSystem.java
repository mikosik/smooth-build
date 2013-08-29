package org.smoothbuild.testing;

import static org.smoothbuild.plugin.Path.path;
import static org.smoothbuild.testing.TestingFileContent.assertFileContent;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;

import org.smoothbuild.fs.mem.InMemoryFileSystem;
import org.smoothbuild.plugin.Path;

public class TestingFileSystem extends InMemoryFileSystem {

  /**
   * Creates textual file that contains its path.
   */
  public void createFileContainingPath(String root, String path) throws IOException {
    createFileWithContent(fullPath(root, path), path);
  }

  public void createFileWithContent(String path, String content) throws IOException {
    createFileWithContent(path(path), content);
  }

  private void createFileWithContent(Path path, String content) throws IOException {
    OutputStreamWriter writer = new OutputStreamWriter(createOutputStream(path));
    writer.write(content);
    writer.close();
  }

  public void createEmptyFile(String path) throws IOException {
    OutputStreamWriter writer = new OutputStreamWriter(createOutputStream(path(path)));
    writer.close();
  }

  public void assertFileContainsItsPath(String root, String path) throws IOException,
      FileNotFoundException {
    Path fullPath = fullPath(root, path);
    InputStream inputStream = this.createInputStream(fullPath);
    assertFileContent(inputStream, path);
  }

  private static Path fullPath(String root, String path) {
    return path(root).append(path(path));
  }
}
