package org.smoothbuild.io.fs.disk;

import static okio.Okio.buffer;
import static okio.Okio.sink;
import static org.smoothbuild.io.fs.disk.RecursiveDeleter.deleteRecursively;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.junit.After;
import org.junit.Before;
import org.smoothbuild.io.fs.base.GenericFileSystemTestCase;
import org.smoothbuild.io.fs.base.Path;

import okio.BufferedSink;
import okio.ByteString;

public class DiskFileSystemTest extends GenericFileSystemTestCase {
  private File tempDir;

  @Before
  public void before() {
    tempDir = com.google.common.io.Files.createTempDir();
    fileSystem = new DiskFileSystem(Paths.get(tempDir.getAbsolutePath()));
  }

  @After
  public void after() throws IOException {
    java.nio.file.Path tempPath = tempDir.toPath();
    if (Files.isDirectory(tempPath)) {
      deleteRecursively(tempPath);
    }
  }

  @Override
  protected void createFile(Path path, ByteString content) throws IOException {
    File file = stringPathToFile(path.value());
    file.getParentFile().mkdirs();
    try (BufferedSink sink = buffer(sink(file))) {
      sink.write(content);
    }
  }

  private File stringPathToFile(String stringPath) {
    return new File(tempDir.getAbsoluteFile() + "/" + stringPath);
  }
}
