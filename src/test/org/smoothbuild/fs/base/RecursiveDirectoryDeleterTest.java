package org.smoothbuild.fs.base;

import static org.assertj.core.api.Assertions.assertThat;
import static org.smoothbuild.testing.TestingJdkFile.createDir;
import static org.smoothbuild.testing.TestingJdkFile.createEmptyFile;

import java.io.File;
import java.io.IOException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.common.io.Files;

public class RecursiveDirectoryDeleterTest {
  private File tempDir;

  private File fileOutside;
  private File mainDir;
  private File directFile;
  private File directDir;
  private File notDirectFile;
  private File notDirectDir;

  @Before
  public void before() throws IOException {
    tempDir = Files.createTempDir();

    File root = tempDir;
    fileOutside = createEmptyFile(root, "fileOutsideMain");

    String mainDirName = "mainDir";
    mainDir = createDir(root, mainDirName);

    String directFileName = "directFile";
    directFile = createEmptyFile(mainDir, directFileName);

    String directDirName = "directDir";
    directDir = createDir(mainDir, directDirName);

    String notDirectFileName = "notDirectFile";
    notDirectFile = createEmptyFile(directDir, notDirectFileName);

    String notDirectDirName = "notDirectDir";
    notDirectDir = createDir(directDir, notDirectDirName);
  }

  @After
  public void after() {
    deleteIfExists(notDirectDir);
    deleteIfExists(notDirectFile);
    deleteIfExists(directDir);
    deleteIfExists(directFile);
    deleteIfExists(mainDir);
    deleteIfExists(fileOutside);
    deleteIfExists(tempDir);
  }

  private static void deleteIfExists(File file) {
    if (file.exists()) {
      file.delete();
    }
  }

  @Test
  public void filesFromReturnsAllFilesRecursively() throws Exception {
    // when
    RecursiveDirectoryDeleter.deleteRecursively(mainDir);

    // then
    assertThat(fileOutside.exists()).isTrue();

    assertThat(mainDir.exists()).isFalse();
    assertThat(directFile.exists()).isFalse();
    assertThat(directDir.exists()).isFalse();
    assertThat(notDirectFile.exists()).isFalse();
    assertThat(notDirectDir.exists()).isFalse();
  }
}
