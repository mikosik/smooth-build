package org.smoothbuild.io.fs.disk;

import static org.assertj.core.api.Assertions.assertThat;
import static org.smoothbuild.io.fs.base.Path.path;
import static org.smoothbuild.io.fs.disk.RecursiveDeleter.deleteRecursively;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.io.fs.mem.GenericFileSystemTestCase;
import org.smoothbuild.testing.common.JdkFileTester;

import com.google.common.io.Files;

public class DiskFileSystemTest extends GenericFileSystemTestCase {
  private File tempDirectory;

  @Before
  public void before() {
    tempDirectory = Files.createTempDir();
    fileSystem = new DiskFileSystem(tempDirectory.getAbsolutePath());
    content = "file content";
    path = path("my/dir/myFile");
  }

  @After
  public void after() throws IOException {
    deleteRecursively(tempDirectory.toPath());
  }

  @Test
  public void openOutputStreamReturnsBufferedStream() throws Exception {
    assertThat(fileSystem.openOutputStream(path)).isInstanceOf(BufferedOutputStream.class);
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

  protected void createFile(String stringPath, String content) throws IOException {
    File file = stringPathToFile(stringPath);
    file.getParentFile().mkdirs();
    JdkFileTester.createFileContent(file, content);
  }

  private File stringPathToFile(String stringPath) {
    return new File(tempDirectory.getAbsoluteFile() + "/" + stringPath);
  }
}
