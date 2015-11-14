package org.smoothbuild.testing.common;

import static org.testory.Testory.given;
import static org.testory.Testory.then;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.when;

import java.io.File;
import java.io.IOException;

import org.junit.Test;

public class TestCaseWithTempDirTest {
  private TestCaseWithTempDir testCase;
  private File tempDir;
  private File newSubDir;

  @Test
  public void temporary_dir_exists() {
    given(testCase = new TestCaseWithTempDir());
    when(testCase.getTempDir()).exists();
    thenReturned(true);
  }

  @Test
  public void temporary_dir_is_deleted_by_after_method() throws IOException {
    given(testCase = new TestCaseWithTempDir());
    given(tempDir = testCase.getTempDir());
    when(testCase).after();
    then(!tempDir.exists());
  }

  @Test
  public void created_content_is_deleted_by_after_method() throws IOException {
    given(testCase = new TestCaseWithTempDir());
    given(tempDir = testCase.getTempDir());
    given(newSubDir = new File(tempDir, "newFile"));
    given(newSubDir).mkdirs();
    when(testCase).after();
    then(!newSubDir.exists());
  }
}
