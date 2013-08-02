package org.smoothbuild.fs.mem;

import java.io.IOException;
import java.io.OutputStreamWriter;

import org.smoothbuild.fs.base.PathUtils;

public class TestingFileSystem extends InMemoryFileSystem {

  public void createFile(String root, String path) throws IOException {
    String fullPath = PathUtils.append(root, path);
    OutputStreamWriter writer = new OutputStreamWriter(createOutputStream(fullPath));
    writer.write(path);
    writer.close();
  }
}
