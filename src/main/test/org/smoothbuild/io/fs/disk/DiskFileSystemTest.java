package org.smoothbuild.io.fs.disk;

import static org.hamcrest.Matchers.instanceOf;
import static org.smoothbuild.io.fs.disk.RecursiveDeleter.deleteRecursively;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.when;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.io.fs.mem.GenericFileSystemTestCase;
import org.smoothbuild.testing.common.JdkFileTester;

public class DiskFileSystemTest extends GenericFileSystemTestCase {
  private File tempDirectory;

  @Before
  public void before() {
    tempDirectory = com.google.common.io.Files.createTempDir();
    fileSystem = new DiskFileSystem(tempDirectory.getAbsolutePath());
  }

  @After
  public void after() throws IOException {
    java.nio.file.Path tempPath = tempDirectory.toPath();
    if (Files.isDirectory(tempPath)) {
      deleteRecursively(tempPath);
    }
  }

  @Test
  public void openOutputStreamReturnsBufferedStream() throws Exception {
    when(fileSystem.openOutputStream(path));
    thenReturned(instanceOf(BufferedOutputStream.class));
  }

  @Override
  protected void createEmptyFile(Path path) throws IOException {
    createEmptyFile(path.value());
  }

  @Override
  protected void createEmptyFile(String stringPath) throws IOException {
    createFile(stringPath, "");
  }

  @Override
  protected void createFile(Path path, String content) throws IOException {
    createFile(path.value(), content);
  }

  private void createFile(String stringPath, String content) throws IOException {
    File file = stringPathToFile(stringPath);
    file.getParentFile().mkdirs();
    JdkFileTester.createFileContent(file, content);
  }

  private File stringPathToFile(String stringPath) {
    return new File(tempDirectory.getAbsoluteFile() + "/" + stringPath);
  }
}
