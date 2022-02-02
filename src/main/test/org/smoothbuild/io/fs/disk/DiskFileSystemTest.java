package org.smoothbuild.io.fs.disk;

import static java.nio.file.Files.createDirectories;
import static okio.Okio.buffer;
import static okio.Okio.sink;

import java.io.IOException;
import java.nio.file.Path;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.io.TempDir;
import org.smoothbuild.io.fs.base.GenericFileSystemTestCase;
import org.smoothbuild.io.fs.base.PathS;

import okio.BufferedSink;
import okio.ByteString;

public class DiskFileSystemTest extends GenericFileSystemTestCase {
  private Path tempDir;

  @BeforeEach
  public void before(@TempDir Path tempDir) {
    this.tempDir = tempDir;
    fileSystem = new DiskFileSystem(tempDir);
  }

  @Override
  protected void createFile(PathS path, ByteString content) throws IOException {
    Path filePath = tempDir.resolve(path.toString());
    createDirectories(filePath.getParent());
    try (BufferedSink sink = buffer(sink(filePath))) {
      sink.write(content);
    }
  }
}
