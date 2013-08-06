package org.smoothbuild.lang.internal;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.smoothbuild.lang.type.Path.path;
import static org.smoothbuild.testing.TestingFileContent.writeAndClose;

import org.junit.Test;
import org.smoothbuild.fs.base.FileSystem;
import org.smoothbuild.lang.type.Path;
import org.smoothbuild.testing.TestingFileSystem;

public class FileRwImplTest {
  Path rootDir = path("abc/efg");
  Path filePath = path("123/456");
  Path fullPath = rootDir.append(filePath);

  FileSystem fileSystem = new TestingFileSystem();

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
    assertThat(fileRwImpl.fullPath()).isEqualTo(fullPath);
  }

  @Test
  public void createOutputStream() throws Exception {
    writeAndClose(fileRwImpl.createOutputStream(), "123/456");
    FileRoImplTest.assertContentHasFilePath(fileRwImpl);
  }
}
