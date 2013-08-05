package org.smoothbuild.fs.mem;

import java.io.IOException;
import java.io.OutputStreamWriter;

import org.smoothbuild.fs.base.PathUtils;

public class TestingFileSystem extends InMemoryFileSystem {

  /**
   * Creates textual file that contains its path.
   */
  public void createFile(String root, String path) throws IOException {
    String fullPath = PathUtils.append(root, path);
    OutputStreamWriter writer = new OutputStreamWriter(createOutputStream(fullPath));
    writer.write(path);
    writer.close();
  }

  public void createEmptyFile(String path) throws IOException {
    OutputStreamWriter writer = new OutputStreamWriter(createOutputStream(path));
    writer.close();
  }
}
