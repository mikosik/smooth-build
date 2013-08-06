package org.smoothbuild.lang.internal;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.smoothbuild.fs.base.PathUtils.SEPARATOR;
import static org.smoothbuild.lang.type.Path.path;
import static org.smoothbuild.testing.TestingFileContent.writeAndClose;

import org.junit.Test;
import org.smoothbuild.fs.base.FileSystem;
import org.smoothbuild.lang.type.Path;
import org.smoothbuild.testing.TestingFileSystem;

public class FileRwImplTest {
  private static final String ROOT_DIR = "abc/efg";
  private static final String FILE_PATH = "123/456";
  private static final String FULL_PATH = ROOT_DIR + SEPARATOR + FILE_PATH;

  FileSystem fileSystem = new TestingFileSystem();
  Path rootDir = path(ROOT_DIR);
  Path filePath = path(FILE_PATH);

  FileRwImpl fileRwImpl = new FileRwImpl(fileSystem, rootDir, filePath);

  @Test
  public void fileSystem() throws Exception {
    assertThat(fileRwImpl.fileSystem()).isEqualTo(fileSystem);
  }

  @Test
  public void testPath() throws Exception {
    assertThat(fileRwImpl.path()).isEqualTo(filePath);
  }

  @Test
  public void fullPath() {
    assertThat(fileRwImpl.fullPath()).isEqualTo(path(FULL_PATH));
  }

  @Test
  public void createOutputStream() throws Exception {
    writeAndClose(fileRwImpl.createOutputStream(), FILE_PATH);
    FileRoImplTest.assertContentHasFilePath(fileRwImpl);
  }
}
