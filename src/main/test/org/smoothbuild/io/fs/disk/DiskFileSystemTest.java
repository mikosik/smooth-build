package org.smoothbuild.io.fs.disk;

import static okio.Okio.buffer;
import static okio.Okio.sink;
import static org.smoothbuild.io.fs.disk.RecursiveDeleter.deleteRecursively;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.smoothbuild.io.fs.base.GenericFileSystemTestCase;
import org.smoothbuild.io.fs.base.Path;

import okio.BufferedSink;
import okio.ByteString;

public class DiskFileSystemTest extends GenericFileSystemTestCase {
  private File tempDir;

  @BeforeEach
  public void before() {
    tempDir = com.google.common.io.Files.createTempDir();
    fileSystem = new DiskFileSystem(java.nio.file.Path.of(tempDir.getAbsolutePath()));
  }

  @AfterEach
  public void after() throws IOException {
    java.nio.file.Path tempPath = tempDir.toPath();
    if (Files.isDirectory(tempPath)) {
      deleteRecursively(tempPath);
    }
  }

  @Override
  protected void createFile(Path path, ByteString content) throws IOException {
    File file = stringPathToFile(path.toString());
    file.getParentFile().mkdirs();
    try (BufferedSink sink = buffer(sink(file))) {
      sink.write(content);
    }
  }

  private File stringPathToFile(String stringPath) {
    return new File(tempDir.getAbsoluteFile() + "/" + stringPath);
  }
}
