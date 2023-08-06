package org.smoothbuild.common.filesystem.disk;

import static java.nio.file.Files.createDirectories;
import static okio.Okio.buffer;
import static okio.Okio.sink;

import java.io.IOException;
import java.nio.file.Path;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.io.TempDir;
import org.smoothbuild.common.filesystem.base.AbstractFileSystemTestSuite;
import org.smoothbuild.common.filesystem.base.PathS;

import okio.BufferedSink;
import okio.ByteString;

public class DiskFileSystemTest extends AbstractFileSystemTestSuite {
  private Path tempDir;

  @BeforeEach
  public void before(@TempDir Path tempDir) {
    this.tempDir = tempDir.resolve("dir");
    fileSystem = new DiskFileSystem(this.tempDir);
  }

  @Override
  protected void createFile(PathS path, ByteString content) throws IOException {
    Path filePath = tempDir.resolve(path.toString());
    createDirectories(filePath.getParent());
    try (BufferedSink sink = buffer(sink(filePath))) {
      sink.write(content);
    }
  }

  @Override
  protected String resolve(PathS path) {
    return path.q();
  }
}
