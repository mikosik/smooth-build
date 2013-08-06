package org.smoothbuild.testing;

import static com.google.common.base.Preconditions.checkArgument;
import static org.smoothbuild.fs.base.PathUtils.isValid;
import static org.smoothbuild.fs.base.PathUtils.toCanonical;
import static org.smoothbuild.testing.TestingFileContent.assertFileContent;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;

import org.smoothbuild.fs.base.PathUtils;
import org.smoothbuild.fs.mem.InMemoryFileSystem;

public class TestingFileSystem extends InMemoryFileSystem {

  /**
   * Creates textual file that contains its path.
   */
  public void createFile(String root, String path) throws IOException {
    String fullPath = fullPath(root, path);

    OutputStreamWriter writer = new OutputStreamWriter(createOutputStream(fullPath));
    writer.write(path);
    writer.close();
  }

  public void createEmptyFile(String path) throws IOException {
    OutputStreamWriter writer = new OutputStreamWriter(createOutputStream(path));
    writer.close();
  }

  public void assertContentHasFilePath(String root, String path) throws IOException,
      FileNotFoundException {
    String fullPath = fullPath(root, path);

    InputStream inputStream = this.createInputStream(fullPath);
    assertFileContent(inputStream, path);
  }

  private static String fullPath(String root, String path) {
    checkArgument(isValid(root));
    checkArgument(isValid(path));

    return PathUtils.append(toCanonical(root), toCanonical(path));
  }
}
