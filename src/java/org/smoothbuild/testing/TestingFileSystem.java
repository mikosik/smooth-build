package org.smoothbuild.testing;

import static com.google.common.base.Preconditions.checkArgument;
import static org.smoothbuild.fs.base.PathUtils.isValid;
import static org.smoothbuild.fs.base.PathUtils.toCanonical;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import org.smoothbuild.fs.base.PathUtils;
import org.smoothbuild.fs.mem.InMemoryFileSystem;

import com.google.common.io.LineReader;

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

    try (InputStreamReader readable = new InputStreamReader(this.createInputStream(fullPath));) {
      LineReader reader = new LineReader(readable);
      String actual = reader.readLine();
      if (!actual.equals(path)) {
        throw new AssertionError("File content is incorrect. Expected '" + path + "' but was '"
            + actual + "'.");
      }
    }
  }

  private static String fullPath(String root, String path) {
    checkArgument(isValid(root));
    checkArgument(isValid(path));

    return PathUtils.append(toCanonical(root), toCanonical(path));
  }
}
