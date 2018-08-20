package org.smoothbuild.io.fs.mem;

import static org.smoothbuild.io.fs.base.Path.path;

import java.io.IOException;

import org.junit.Before;
import org.smoothbuild.io.fs.base.GenericFileSystemTestCase;
import org.smoothbuild.io.fs.base.Path;

import okio.BufferedSink;

public class MemoryFileSystemTest extends GenericFileSystemTestCase {
  @Before
  public void before() {
    fileSystem = new MemoryFileSystem();
  }

  // helpers

  @Override
  protected void createEmptyFile(String path) throws IOException {
    createEmptyFile(path(path));
  }

  @Override
  protected void createEmptyFile(Path path) throws IOException {
    createFile(path, new byte[] {});
  }

  @Override
  protected void createFile(Path path, byte[] content) throws IOException {
    try (BufferedSink sink = fileSystem.sink(path)) {
      sink.write(content);
    }
  }
}
