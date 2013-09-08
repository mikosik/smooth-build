package org.smoothbuild.testing.common;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.IOException;

import org.junit.Test;
import org.smoothbuild.testing.common.TestCaseWithTempDir;

public class TestCaseWithTempDirTest {

  @Test
  public void temporaryDirectoryExists() {
    // given
    TestCaseWithTempDir testCase = new TestCaseWithTempDir();

    // when
    File tempDir = testCase.getTempDirectory();

    // then
    assertThat(tempDir.exists()).isTrue();

    // cleanup temporary dir
    tempDir.delete();
  }

  @Test
  public void temporaryDirectoryIsDeletedInAfterMethod() throws IOException {
    TestCaseWithTempDir testCase = new TestCaseWithTempDir();
    File tempDir = testCase.getTempDirectory();
    testCase.after();

    assertThat(tempDir.exists()).isFalse();
  }

  @Test
  public void createdContentIsDeletedInAfterMethod() throws IOException {
    TestCaseWithTempDir testCase = new TestCaseWithTempDir();
    File tempDir = testCase.getTempDirectory();
    File newSubDir = new File(tempDir, "newFile");
    newSubDir.mkdirs();
    assertThat(newSubDir.exists()).isTrue();

    testCase.after();

    assertThat(newSubDir.exists()).isFalse();
  }
}
