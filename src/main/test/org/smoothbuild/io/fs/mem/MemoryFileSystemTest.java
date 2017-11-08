package org.smoothbuild.io.fs.mem;

import static org.smoothbuild.io.fs.base.Path.path;
import static org.smoothbuild.util.Streams.writeAndClose;

import java.io.IOException;
import java.io.OutputStream;

import org.junit.Before;
import org.smoothbuild.io.fs.base.GenericFileSystemTestCase;
import org.smoothbuild.io.fs.base.Path;

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
    OutputStream outputStream = fileSystem.openOutputStream(path);
    writeAndClose(outputStream, content);
  }
}
