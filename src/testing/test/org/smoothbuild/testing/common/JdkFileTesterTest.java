package org.smoothbuild.testing.common;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.FileInputStream;

import org.junit.Test;

public class JdkFileTesterTest extends TestCaseWithTempDir {
  File root = getTempDirectory();

  @Test
  public void testCreateDir() {
    String dirName = "myDir";
    File created = JdkFileTester.createDir(root, dirName);

    File expected = new File(root, dirName);
    assertThat(expected.exists()).isTrue();
    assertThat(created).isEqualTo(expected);
  }

  @Test
  public void testCreateEmptyFile() throws Exception {
    String fileName = "fileName";
    File created = JdkFileTester.createEmptyFile(root, fileName);

    StreamTester.assertContent(new FileInputStream(created), "");
  }

  @Test
  public void testCreateFileContent() throws Exception {
    String fileName = "fileName";
    String content = "content";
    File created = JdkFileTester.createFileContent(root, fileName, content);

    StreamTester.assertContent(new FileInputStream(created), content);
  }
}
