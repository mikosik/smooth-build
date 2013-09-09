package org.smoothbuild.testing.common;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import org.junit.Test;
import org.smoothbuild.testing.common.JdkFileTester;
import org.smoothbuild.testing.common.StreamTester;
import org.smoothbuild.testing.common.TestCaseWithTempDir;

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

  @Test
  public void assertContentSucceedsWhenContentMatches() throws Exception {
    String fileName = "fileName";
    String content = "content";
    File file = new File(root, fileName);
    StreamTester.writeAndClose(new FileOutputStream(file), content);

    JdkFileTester.assertContent(root, fileName, content);
  }

  @Test
  public void assertContentFailsWhenContentDoesNotMatch() throws Exception {
    String fileName = "fileName";
    String content = "content";
    File file = new File(root, fileName);
    StreamTester.writeAndClose(new FileOutputStream(file), content);

    try {
      JdkFileTester.assertContent(root, fileName, "other contet");
    } catch (AssertionError e) {
      // expected
      return;
    }
    fail("exception should be thrown");
  }
}
