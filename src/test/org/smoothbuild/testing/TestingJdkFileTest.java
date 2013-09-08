package org.smoothbuild.testing;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import org.junit.Test;
import org.smoothbuild.testing.common.StreamTester;

public class TestingJdkFileTest extends TestCaseWithTempDir {
  File root = getTempDirectory();

  @Test
  public void testCreateDir() {
    String dirName = "myDir";
    File created = TestingJdkFile.createDir(root, dirName);

    File expected = new File(root, dirName);
    assertThat(expected.exists()).isTrue();
    assertThat(created).isEqualTo(expected);
  }

  @Test
  public void testCreateEmptyFile() throws Exception {
    String fileName = "fileName";
    File created = TestingJdkFile.createEmptyFile(root, fileName);

    StreamTester.assertContent(new FileInputStream(created), "");
  }

  @Test
  public void testCreateFileContent() throws Exception {
    String fileName = "fileName";
    String content = "content";
    File created = TestingJdkFile.createFileContent(root, fileName, content);

    StreamTester.assertContent(new FileInputStream(created), content);
  }

  @Test
  public void assertContentSucceedsWhenContentMatches() throws Exception {
    String fileName = "fileName";
    String content = "content";
    File file = new File(root, fileName);
    StreamTester.writeAndClose(new FileOutputStream(file), content);

    TestingJdkFile.assertContent(root, fileName, content);
  }

  @Test
  public void assertContentFailsWhenContentDoesNotMatch() throws Exception {
    String fileName = "fileName";
    String content = "content";
    File file = new File(root, fileName);
    StreamTester.writeAndClose(new FileOutputStream(file), content);

    try {
      TestingJdkFile.assertContent(root, fileName, "other contet");
    } catch (AssertionError e) {
      // expected
      return;
    }
    fail("exception should be thrown");
  }
}
