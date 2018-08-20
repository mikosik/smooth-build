package org.smoothbuild.io.fs.disk;

import static org.smoothbuild.io.fs.disk.RecursiveDeleter.deleteRecursively;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.junit.After;
import org.junit.Before;
import org.smoothbuild.io.fs.base.GenericFileSystemTestCase;
import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.util.Streams;

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
  protected void createEmptyFile(Path path) throws IOException {
    createEmptyFile(path.value());
  }

  @Override
  protected void createEmptyFile(String stringPath) throws IOException {
    createFile(stringPath, new byte[] {});
  }

  @Override
  protected void createFile(Path path, byte[] content) throws IOException {
    createFile(path.value(), content);
  }

  private void createFile(String stringPath, byte[] content) throws IOException {
    File file = stringPathToFile(stringPath);
    file.getParentFile().mkdirs();
    FileOutputStream outputStream = new FileOutputStream(file);
    Streams.writeAndClose(outputStream, content);
  }

  private File stringPathToFile(String stringPath) {
    return new File(tempDir.getAbsoluteFile() + "/" + stringPath);
  }
}
