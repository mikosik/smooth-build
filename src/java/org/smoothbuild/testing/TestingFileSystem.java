package org.smoothbuild.testing;

import static org.smoothbuild.lang.type.Path.path;
import static org.smoothbuild.testing.TestingFileContent.assertFileContent;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;

import org.smoothbuild.fs.mem.InMemoryFileSystem;
import org.smoothbuild.lang.type.Path;

public class TestingFileSystem extends InMemoryFileSystem {

  /**
   * Creates textual file that contains its path.
   */
  public void createFile(String root, String path) throws IOException {
    Path fullPath = fullPath(root, path);
    OutputStreamWriter writer = new OutputStreamWriter(createOutputStream(fullPath));
    writer.write(path);
    writer.close();
  }

  public void createEmptyFile(String path) throws IOException {
    OutputStreamWriter writer = new OutputStreamWriter(createOutputStream(path(path)));
    writer.close();
  }

  public void assertContentHasFilePath(String root, String path) throws IOException,
      FileNotFoundException {
    Path fullPath = fullPath(root, path);
    InputStream inputStream = this.createInputStream(fullPath);
    assertFileContent(inputStream, path);
  }

  private static Path fullPath(String root, String path) {
    return path(root).append(path(path));
  }
}
